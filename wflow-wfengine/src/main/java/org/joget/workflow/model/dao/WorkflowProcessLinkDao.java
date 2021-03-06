package org.joget.workflow.model.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.workflow.model.WorkflowProcessLink;

public class WorkflowProcessLinkDao extends AbstractSpringDao {

    public final static String ENTITY_NAME="WorkflowProcessLink";

    public void addWorkflowProcessLink(WorkflowProcessLink wfProcessLink){
        saveOrUpdate(ENTITY_NAME, wfProcessLink);
    }

    public WorkflowProcessLink getWorkflowProcessLink(String processId){
        return (WorkflowProcessLink) find(ENTITY_NAME, processId);
    }

    public void delete(WorkflowProcessLink wfProcessLink) {
        super.delete(ENTITY_NAME, wfProcessLink);
    }
    
    public Map<String, Collection<String>> getOriginalIds(Collection<String> ids) {
        Map<String, Collection<String>> originalIds = new HashMap<String, Collection<String>>();
        Collection<String> existIds = new ArrayList<String>();
        
        if (ids.size() > 0) {
            String conditions = "where e.processId in (";
            for (String id : ids) {
                conditions += "?,";
            }
            conditions = conditions.substring(0, conditions.length() - 1) + ")";
            Collection<WorkflowProcessLink> links = super.find(ENTITY_NAME, conditions, ids.toArray(new String[0]), null, null, null, null);
            
            for (WorkflowProcessLink link : links) {
                String orgId = link.getOriginProcessId();
                String id = link.getProcessId();
                
                Collection<String> pIds = originalIds.get(orgId);
                if (pIds == null) {
                    pIds = new ArrayList<String>();
                }
                pIds.add(id);
                existIds.add(id);
                
                originalIds.put(orgId, pIds);
            }
            
            // for those does not has link
            for (String id : ids) {
                if (!existIds.contains(id)) {
                    Collection<String> pIds = originalIds.get(id);
                    if (pIds == null) {
                        pIds = new ArrayList<String>();
                    }
                    pIds.add(id);
                    existIds.add(id);
                    originalIds.put(id, pIds);
                }
            }
        }
        
        return originalIds;
    }
}
