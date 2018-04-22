package dao;

import com.bw.workorder.enumeration.GenderConstant;
import com.bw.workorder.entity.PortalAccount;
import com.bw.workorder.entity.UserData;
import com.bw.workorder.enumeration.PortalAccountType;

import com.bw.workorder.enumeration.GoalStatusConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import com.bw.workorder.enumeration.GenericStatusConstant;
import com.bw.workorder.entity.Organisation;

import com.bw.workorder.entity.*;
import com.bw.workorder.enumeration.*;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import extractors.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import pojos.ApiResourcePortalUser;
import pojos.ApiResponse;
import pojos.ApiResponseClient;
import pojos.UserResponsibilityPojo;
import utils.GeneralConstants;
import utils.NetworkRequestUtil;
import utils.ResourceUtil;
import utils.WorkOrderUtil;
import utils.sequence.SequenceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static utils.GeneralConstants.BASE_ACCOUNTS_URL;

@SuppressWarnings("unchecked")
public class PortalUserDao extends BaseDao {
    @Inject
    private OrganisationDao organisationDao;

    public void createDefaultAccount() {


    }

    public WorkOrderUserData assignWorkOrder(UserResponsibilityPojo assignee, WorkOrder workOrder, UserData assignedTo, Session session) {
        WorkOrderUserData workOrderUserData = new WorkOrderUserData();
        workOrderUserData.setResponsibility(ResponsibilityTypeConstant.fromString(assignee.getResponsibility()));
        workOrderUserData.setDateCreated(WorkOrderUtil.nowToTimeStamp());
        workOrderUserData.setStatus(GenericStatusConstant.ACTIVE);
        workOrderUserData.setWorkOrder(workOrder);
        workOrderUserData.setAssignee(assignedTo);

        session.save(workOrderUserData);

        return workOrderUserData;
    }

    public List<UserData> getUsersByIds(List<Long> ids) {
        return transactionManager.doForResult(session ->
                session.createCriteria(UserData.class)
                        .add(Restrictions.in("id", ids))
                        .list()
        );
    }

    public boolean emailExists(String email) {
        return transactionManager.doForResult(session -> (Long) session.createCriteria(UserData.class)
                .add(Restrictions.eq("email", email))
                .setProjection(Projections.rowCount())
                .uniqueResult() > 0);
    }

    public UserData getUserByUserId(String userId) {
        return getUniqueRecordByProperty(UserData.class, "userId", userId);
    }

    public Long getUserClosedOrderCount(UserData userData) {
        return (Long) transactionManager.doForResult(session -> {
            String query = "select count(w) from WorkOrder w,WorkOrderUserData wp where w.workOrderStatus='" + WorkOrderStatusConstant.CLOSED +
                    "' and wp.workOrder.id=w.id and wp.assignee.id=" + userData.getId();

            return workOrderService.getUniqueRecordByHQL(query);

        });
    }

    public long getUserPendingOrderCount(UserData userData) {
        return (Long) transactionManager.doForResult(session -> {
            String query = "select count(w) from WorkOrder w,WorkOrderUserData wp where w.workOrderStatus !='" + WorkOrderStatusConstant.CLOSED +
                    "' and wp.workOrder.id=w.id and wp.assignee.id=" + userData.getId();

            return workOrderService.getUniqueRecordByHQL(query);

        });
    }

    public List<WorkOrder> getUserPendingOrder(UserData userData, int start, int length) {
        return (List<WorkOrder>) transactionManager.doForResult(session -> {
            String query = "select w from WorkOrder w,WorkOrderUserData wp where w.workOrderStatus !='" + WorkOrderStatusConstant.CLOSED +
                    "' and wp.workOrder.id=w.id and wp.assignee.id=" + userData.getId();

            return workOrderService.getAllRecordsByHQL(query, start, length);

        });
    }

    public long getUserCreatedOrderCount(UserData userData) {
        return (Long) transactionManager.doForResult(session -> {
            String query = "select count(w) from WorkOrder w where w.createdBy.id=" + userData.getId();

            return workOrderService.getUniqueRecordByHQL(query);

        });
    }

    public List<WorkOrder> getAssigneeOrderByStatus(String status, UserData user, int start, int length) {
        return (List<WorkOrder>) transactionManager.doForResult(session -> {
            String query = "select w from WorkOrder w,WorkOrderUserData wp" +
                    " where wp.workOrder.id=w.id and wp.assignee.id=" + user.getId();
            if (status != null && !status.equalsIgnoreCase("all")) {
                query += " and w.workOrderStatus ='" + status + "'";
            }

            return workOrderService.getAllRecordsByHQL(query, start, length);

        });
    }

    public List<WorkOrder> getOrdersCreatedByLoggedInUserByStatus(String status, UserData user, int start, int length) {
        return transactionManager.doForResult(session -> {
                    Criteria criteria = session.createCriteria(WorkOrder.class);
                    if (!status.equalsIgnoreCase("all")) {
                        criteria.add(Restrictions.eq("workOrderStatus", status));

                    }
                    criteria.add(Restrictions.eq("createdBy.id", user.getId()))
                            .add(Restrictions.eq("status", GenericStatusConstant.ACTIVE))
                            .setMaxResults(length)
                            .setFirstResult(start);
                    return criteria.list();
                }
        );
    }

//    public UserData createUser(UserData portalUser, UserOrganisation userOrganisationBranch) {
////        return transactionManager.doForResult(session -> {
////            session.save(portalUser);
////            session.save(userOrganisationBranch);
////            return portalUser;
////        });
//    }
}
