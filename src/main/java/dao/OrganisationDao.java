package dao;

import com.bw.workorder.entity.*;
import com.bw.workorder.enumeration.GenericStatusConstant;
import com.bw.workorder.enumeration.GoalStatusConstant;
import com.bw.workorder.enumeration.PortalAccountType;
import com.bw.workorder.enumeration.RoleTypeConstant;
import ninja.jpa.UnitOfWork;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

@SuppressWarnings("unchecked")
public class OrganisationDao extends BaseDao {

    public Organisation createOrganisation(Organisation organisation) {
        return saveObject(organisation);
    }

    public Organisation getUserOrganisation(UserData user) {
        String q = "select o from Organisation o,Membership m where m.userData.id=" + user.getId() +
                " and m.portalAccount.accountType='" + PortalAccountType.ORGANISATION + "' and m.status='" + GenericStatusConstant.ACTIVE + "'" +
                " and o.portalAccount.id=m.portalAccount.id";
        return (Organisation) workOrderService.getUniqueRecordByHQL(q);
    }

    public List<Branch> getOrganisationBranchesByUser(UserData user) {
        String q = "select b from Branch b, Organisation o,Membership m where m.userData.id=" + user.getId() +
                " and m.portalAccount.accountType='" + PortalAccountType.ORGANISATION + "' and m.status='" + GenericStatusConstant.ACTIVE + "'" +
                " and o.portalAccount.id=m.portalAccount.id  and b.organisation.id=o.id";
        return (List<Branch>) workOrderService.getAllRecordsByHQL(q);
    }

    public List<Branch> getOrganisationBranches(Organisation organisation) {
        return (List<Branch>) transactionManager.doForResult(session ->
                session.createCriteria(Branch.class)
                        .add(Restrictions.eq("organisation", organisation))
                        .add(Restrictions.eq("status", GenericStatusConstant.ACTIVE))
                        .list()
        );
    }

    public List<UserData> getUsersByOrganisation(Organisation organisation,String filter, Integer start, Integer length) {
        String q = "select u from UserData u,Organisation o,Membership m where o.id=" + organisation.getId() +
                " and o.portalAccount.id=m.portalAccount.id and m.status='" + GenericStatusConstant.ACTIVE +
                "' and u.id=m.userData.id and lower(u.displayName) like lower('%"+ filter +"%')";
        return (List<UserData>) workOrderService.getAllRecordsByHQL(q, start, length);
    }

    public List<UserData> getUsersByOrganisation(Organisation organisation) {
        String q = "select u from UserData u,Organisation o,Membership m where o.id=" + organisation.getId() +
                " and o.portalAccount.id=m.portalAccount.id and m.status='" + GenericStatusConstant.ACTIVE +
                "' and u.id=m.userData.id";
        return (List<UserData>) workOrderService.getAllRecordsByHQL(q);
    }

    public long getUsersByOrganisationCount(Organisation organisation) {
        String q = "select count(u) from UserData u,Organisation o,Membership m where o.id=" + organisation.getId() +
                " and o.portalAccount.id=m.portalAccount.id and m.status='" + GenericStatusConstant.ACTIVE +
                "' and u.id=m.userData.id";
        return (long) workOrderService.getUniqueRecordByHQL(q);
    }

    public Organisation createOrganisation(Organisation organisation, List<OrganisationGoal> organisationGoals) {
        return transactionManager.doForResult(session -> {
            session.save(organisation);
            for (OrganisationGoal organisationGoal : organisationGoals) {
                session.save(organisationGoal);
            }
            return organisation;
        });
    }

    public List<OrganisationGoal> getUserOrganisationGoals(UserData user, GenericStatusConstant status) {
        Organisation organisation = getUserOrganisation(user);
        return transactionManager.doForResult(session ->
                session.createCriteria(OrganisationGoal.class)
                        .add(Restrictions.eq("organisation", organisation))
                        .add(Restrictions.eq("status", status))
                        .list()
        );
    }

    public PortalAccount getOrganisationPortalAccount(Organisation organisation) {
        return getById(PortalAccount.class, organisation.getPortalAccount().getId());
    }

    public Branch getOrganisationHQBranch(Organisation organisation) {
        return (Branch) transactionManager.doForResult(session ->
                session.createCriteria(Branch.class)
                        .add(Restrictions.eq("organisation", organisation))
                        .add(Restrictions.eq("status", GenericStatusConstant.ACTIVE))
                        .add(Restrictions.eq("headQuaters", true))
                        .uniqueResult()
        );
    }

    public RoleTypeConstant getUserRoleInOrganisation(UserData userData, Organisation organisation) {
        return (RoleTypeConstant) transactionManager.doForResult(session ->
                session.createCriteria(Membership.class)
                        .setProjection(Projections.property("role"))
                        .add(Restrictions.eq("userData", userData))
                        .add(Restrictions.eq("status", GenericStatusConstant.ACTIVE))
                        .add(Restrictions.eq("portalAccount", organisation.getPortalAccount()))
                        .uniqueResult()
        );
    }
}
