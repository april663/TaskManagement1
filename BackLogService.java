package com.TaskManagement1.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagement1.Entity.Issue;
import com.TaskManagement1.Entity.Sprint;
import com.TaskManagement1.Enum.IssueType;
import com.TaskManagement1.Enum.SprintState;
import com.TaskManagement1.Repository.IssueRepository;
import com.TaskManagement1.Repository.SprintRepository;

@Service
public class BackLogService {
	
	@Autowired
	private SprintRepository sprintRepo;
	
	@Autowired
	private IssueRepository issueRepo;
	
	
	public List<Issue>getBackLog(Long projectId){
		List<Issue> issues= issueRepo.findByProjectIdAndSprintIdIsNullOrderByBackLogPosition(projectId);
		
		return issues;
	}
	
	@Transactional
	public void recorderBackLog(Long projectId,List<Long>orderedIssueId) {
		
		int pos= 0;
		for(Long issueId:orderedIssueId) {
			Issue issue= issueRepo.findById(issueId).orElseThrow(()-> new RuntimeException("Issue not found"));
			
			issue.setBackLogPosition(pos++);
			issueRepo.save(issue);
		}
	}
	
	@Transactional
	public Issue addIssueToSprint(Long issueId,Long sprintId) {
		Issue issue= issueRepo.findById(issueId).orElseThrow(()->new RuntimeException("Issue not found"));
		
		Sprint sprint= sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("Sprint not found"));
		
		SprintState state= sprint.getState();
		
		if(state !=SprintState.PLANNED && state != SprintState.ACTIVE ) {
			throw new RuntimeException("Can not add issue to sprint in state:"+state);
		}
		issue.setSprintId(sprintId);
		issue.setBackLogPosition(null);
		
		return issueRepo.save(issue);
		
		
	}
	
	
	@Transactional(readOnly=true)
	public Map<String,Object>getBackLogHierArchy(Long projectId){
		
		
		List<Issue> backLog= getBackLog(projectId);
		
		Map<Long,Map<String,Object>>epicMap=new LinkedHashMap<>();
		
		Map<Long,Issue>storyMap= new HashMap<>();
		
		
		for(Issue issue:backLog) {
			if(issue.getIssueType()==IssueType.EPIC) {
				Map<String,Object> epicData= new LinkedHashMap<>();
				
				epicData.put("epic", issue);
				epicData.put("stories", new ArrayList<Issue>());
				epicData.put("subTask", new HashMap<Long,List<Issue>>());
				
				epicMap.put(issue.getId(), epicData);
			}
			
			if(issue.getIssueType()==IssueType.STORY) {
				storyMap.put(issue.getId(), issue);
			}
			
		}
		
		for(Issue issue:backLog) {
			if(issue.getIssueType()==IssueType.STORY) {
				Long epicId= issue.getEpicId();
				if(epicId !=null && epicMap.containsKey(epicId)) {
					List<Issue>stories= (List<Issue>)epicMap.get(epicId).get("stories");
					stories.add(issue);
				}
			}
		}
		
		for(Issue issue:backLog) {
			
			if(issue.getIssueType()== IssueType.SUBTASK) {
				Long parentId=issue.getParentIssueId();
			
			if(parentId != null && storyMap.containsKey(parentId)) {
				
				for(Map<String,Object>epicData:epicMap.values()) {
					
					List<Issue> stories= (List<Issue>)epicData.get("stories");
					
					boolean belongsToEpic=stories.stream().anyMatch(s-> s.getId().equals(parentId));
					
					if(belongsToEpic) {
						Map<Long,List<Issue>> subMap= (Map<Long,List<Issue>>)epicData.get("subtask");
						subMap.computeIfAbsent(projectId, k-> new ArrayList<>()).add(issue);
						
						break;
						
					}
				}
			}
		}
		}
		
		return Collections.singletonMap("epic", epicMap);
	}
	
	
	

}
