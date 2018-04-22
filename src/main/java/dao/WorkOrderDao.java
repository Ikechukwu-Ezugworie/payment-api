package dao;

import com.bw.workorder.enumeration.ClosureRequestStatusConstant;
import com.bw.workorder.entity.UserData;

import com.bw.workorder.entity.WorkOrder;

import com.bw.workorder.entity.*;
import com.bw.workorder.enumeration.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import pojos.FeedbackPojo;
import pojos.ReminderPojo;
import pojos.UserResponsibilityPojo;
import pojos.request.WorkOrderRequestPojo;
import utils.GeneralConstants;
import utils.WorkOrderUtil;
import utils.sequence.SequenceService;

public class WorkOrderDao extends BaseDao {
    @Inject
    private PortalUserDao portalUserDao;

    public WorkOrder createWorkOrder(WorkOrderRequestPojo request, UserData user) {

        List<Long> ids = request.getAssignees().stream().map(UserResponsibilityPojo::getId).collect(Collectors.toList());
        List<UserData> portalUsers = portalUserDao.getUsersByIds(ids);
        return transactionManager.doForResult(session -> {
            WorkOrder workOrder = new WorkOrder();
            workOrder.setTitle(request.getTitle());
            workOrder.setWorkOrderId(generateWorkOrderId());
            workOrder.setDescription(request.getDescription());
            workOrder.setStartDate(WorkOrderUtil.getTimestamp(request.getStartDate(), GeneralConstants.DEFAULT_DATE_TIME_FORMAT));
            workOrder.setEndDate(WorkOrderUtil.getTimestamp(request.getEndDate(), GeneralConstants.DEFAULT_DATE_TIME_FORMAT));
            workOrder.setPriority(PriorityConstant.fromValue(request.getPriority()));
            workOrder.setGoal(request.getGoal());
            workOrder.setCreatedBy(request.getCreatedBy());

            session.save(workOrder);

            for (UserResponsibilityPojo assignee : request.getAssignees()) {
                UserData assignedTo = portalUsers.stream().filter(portalUser -> portalUser.getId().equals(assignee.getId())).findFirst().get();
                portalUserDao.assignWorkOrder(assignee, workOrder, assignedTo, session);

                String desc = String.format("WorkOrder #%s was assigned to %s with responsibility %s", workOrder.getWorkOrderId(),
                        assignedTo.getDisplayName(), assignee.getResponsibility());

                ActivityType activityType = getActivityTypeRecord(ActivityTypeConstant.ASSIGN);
                createActivityLog(desc, workOrder, activityType, user, session);
            }

            for (FeedbackPojo feedbackPojo : request.getFeedbackRequests()) {
                FeedbackRequest feedback = new FeedbackRequest();
                feedback.setInstruction(feedbackPojo.getInstruction());
                feedback.setExpectedDate(WorkOrderUtil.getTimestamp(feedbackPojo.getExpectedDate()));
                feedback.setWorkOrder(workOrder);

                session.save(feedback);
            }

            for (ReminderPojo reminderPojo : request.getReminders()) {
                Reminder reminder = new Reminder();
                reminder.setDetails(reminderPojo.getDetails());
                reminder.setDate(WorkOrderUtil.getTimestamp(reminderPojo.getDate()));
                reminder.setWorkOrder(workOrder);

                session.save(workOrder);
            }

            return workOrder;

        });

    }

    private ActivityType getActivityTypeRecord(ActivityTypeConstant activityTypeConstant) {
        return getUniqueRecordByProperty(ActivityType.class, "name", activityTypeConstant);
    }

    public static void createActivityLog(String description, WorkOrder workOrder, ActivityType activityType, UserData actor, Session session) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setDescription(description);
        activityLog.setDateCreated(WorkOrderUtil.nowToTimeStamp());
        activityLog.setWorkOrder(workOrder);
        activityLog.setActor(actor);
        activityLog.setActivityType(activityType);

        session.save(activityLog);
    }

    public void createActivityLog(String description, WorkOrder workOrder, ActivityType activityType, UserData actor) {
        transactionManager.doIntransaction(session ->
                createActivityLog(description, workOrder, activityType, actor, session)
        );
    }

    public String generateWorkOrderId() {
        return transactionManager.doForResult(session -> {
            SequenceService sequenceService = new SequenceService(session, "work_order_id");
            return sequenceService.getNextId("%010d");
        });
    }

    public void createWorkOrder(WorkOrder workOrder, List<WorkOrderUserData> workOrderUserDatas,
                                List<FeedbackRequest> feedbacks, List<Reminder> reminders, UserData createdBy) {
        transactionManager.doIntransaction(session -> {
            session.save(workOrder);
            String wDesc = String.format("Created by %s", createdBy.getDisplayName());

            createActivityLog(wDesc, workOrder, getActivityTypeRecord(ActivityTypeConstant.OPEN), createdBy, session);
            for (WorkOrderUserData workOrderUserData : workOrderUserDatas) {
                String desc = String.format("#%s was assigned %s status",
                        workOrderUserData.getAssignee().getDisplayName(), workOrderUserData.getResponsibility().name());
                session.save(workOrderUserData);

                ActivityType activityType = getActivityTypeRecord(ActivityTypeConstant.ASSIGN);
                createActivityLog(desc, workOrder, activityType, createdBy, session);
            }
            for (FeedbackRequest feedback : feedbacks) {
                session.save(feedback);
            }

            for (Reminder reminder : reminders) {
                session.save(reminder);
            }
        });
    }

    public void requestClosure(WorkOrder order, UserData requestedBy) {
        transactionManager.doIntransaction(session -> {
            order.setWorkOrderStatus(WorkOrderStatusConstant.CLOSURE_REQUESTED);
            session.update(order);

            Criteria criteria = session.createCriteria(ClosureRequest.class)
                    .add(Restrictions.eq("workOrder.id", order.getId()))
                    .add(Restrictions.eq("status", ClosureRequestStatusConstant.PENDING));

            ClosureRequest closureRequest = (ClosureRequest) criteria.uniqueResult();
            if (closureRequest != null) {
                throw new IllegalArgumentException("This order already has a pending closure request");
            }
            closureRequest = new ClosureRequest();
            closureRequest.setDateCreated(WorkOrderUtil.nowToTimeStamp());
            closureRequest.setLastModified(WorkOrderUtil.nowToTimeStamp());
            closureRequest.setStatus(ClosureRequestStatusConstant.PENDING);
            closureRequest.setReason("");
            closureRequest.setWorkOrder(order);
            closureRequest.setRequestedBy(requestedBy);

            session.save(closureRequest);

            ActivityType activityType = getUniqueRecordByProperty(ActivityType.class, "name", ActivityTypeConstant.CLOSE_REQUEST);
            createActivityLog(String.format("%s sent a close request", requestedBy.getDisplayName()), order, activityType, requestedBy, session);

        });
    }

    public void close(WorkOrder order, UserData userData) {
        transactionManager.doIntransaction(session -> {
            order.setWorkOrderStatus(WorkOrderStatusConstant.CLOSED);
            session.update(order);


            Criteria criteria = session.createCriteria(ClosureRequest.class)
                    .add(Restrictions.eq("workOrder.id", order.getId()))
                    .add(Restrictions.eq("status", ClosureRequestStatusConstant.PENDING));

            ClosureRequest closureRequest = (ClosureRequest) criteria.uniqueResult();

            if (closureRequest == null) {
                throw new IllegalArgumentException("Please, send a closure request before attempting to close an order");
            }
            closureRequest.setStatus(ClosureRequestStatusConstant.ACCEPTED);
            closureRequest.setLastModified(WorkOrderUtil.nowToTimeStamp());
            session.update(closureRequest);

            ActivityType activityType = getUniqueRecordByProperty(ActivityType.class, "name", ActivityTypeConstant.CLOSURE);
            createActivityLog(String.format("Order was closed by %s", userData.getDisplayName()), order, activityType, userData, session);
        });
    }

    public void denyCloseRequest(String reason, WorkOrder order, UserData userData) {
        transactionManager.doIntransaction(session -> {
            order.setWorkOrderStatus(WorkOrderStatusConstant.OPEN);
            session.update(order);

            Criteria criteria = session.createCriteria(ClosureRequest.class)
                    .add(Restrictions.eq("workOrder.id", order.getId()))
                    .add(Restrictions.eq("status", ClosureRequestStatusConstant.PENDING));

            ClosureRequest closureRequest = (ClosureRequest) criteria.uniqueResult();

            if (closureRequest == null) {
                throw new IllegalArgumentException("Please, send a closure request first");
            }
            closureRequest.setStatus(ClosureRequestStatusConstant.DENIED);
            closureRequest.setReason(reason);
            closureRequest.setLastModified(WorkOrderUtil.nowToTimeStamp());
            session.update(closureRequest);

            ActivityType activityType = getUniqueRecordByProperty(ActivityType.class, "name", ActivityTypeConstant.CLOSURE);
            createActivityLog(String.format("Close request was denied by %s", userData.getDisplayName()), order, activityType, userData, session);
        });
    }

    public ArrayList<UserData> getWorkOrderAssignees(WorkOrder workOrder) {
        String queryBuilder = "select u from UserData u,WorkOrderUserData wu where u.id=wu.assignee.id and wu.workOrder.id=" + workOrder.getId() +
                " and wu.status='" +GenericStatusConstant.ACTIVE.getValue() + "'";

        System.out.println(queryBuilder);
        return (ArrayList<UserData>) workOrderService.getAllRecordsByHQL(queryBuilder);
    }

    public List<Reminder> getWorkOrderReminders(WorkOrder workOrder) {
        String queryBuilder = "select r from Reminder r where r.workOrder.id=" + workOrder.getId();
        System.out.println(queryBuilder);
        return (List<Reminder>) workOrderService.getAllRecordsByHQL(queryBuilder);
    }

    public List<FeedbackRequest> getWorkOrderFeedbackRequests(WorkOrder workOrder) {
        String queryBuilder = "select r from FeedbackRequest r where r.workOrder.id=" + workOrder.getId();
        System.out.println(queryBuilder);
        return (List<FeedbackRequest>) workOrderService.getAllRecordsByHQL(queryBuilder);
    }

    public ResponsibilityTypeConstant getUserResponsibility(UserData assignee, WorkOrder workOrder) {
        return(ResponsibilityTypeConstant) transactionManager.doForResult(session ->
            session.createCriteria(WorkOrderUserData.class)
                    .setProjection(Projections.property("responsibility"))
                    .add(Restrictions.eq("status",GenericStatusConstant.ACTIVE.getValue()))
                    .add(Restrictions.eq("assignee.id",assignee.getId()))
                    .add(Restrictions.eq("workOrder.id",workOrder.getId()))
                    .uniqueResult()
        );
    }
}
