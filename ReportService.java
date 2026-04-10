/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TaskManagement1.Service;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement1.Entity.Issue;
import com.TaskManagement1.Entity.Sprint;
import com.TaskManagement1.Enum.IssueStatus;
import com.TaskManagement1.Enum.SprintState;
import com.TaskManagement1.Repository.IssueRepository;
import com.TaskManagement1.Repository.SprintRepository;
import java.time.LocalDate;

@Service
public class ReportService {

	
	@Autowired
	private IssueRepository issueRepo;
	
	@Autowired
	private SprintRepository sprintRepo;
	
	
	public Map<String,Object>burnDownData(Long sprintId){
		
		Sprint sprint= sprintRepo.findById(sprintId).orElseThrow(()-> new RuntimeException("sprint not found"));
		List<Issue>issues= issueRepo.findBySprintId(sprintId);
		
		int total=issues.size() ;
		Map<String,Integer> chart= new LinkedHashMap<>();
		
		LocalDate start= sprint.getStartDate();
		LocalDate end=sprint.getEndDate() !=null? sprint.getEndDate():LocalDate.now();
		
		for(LocalDate d=start; !d.isAfter(end);d=d.plusDays(1)) {
			int done=(int)issues.stream().filter(i->"DONE".equals(i.getIssueStatus().name())).count();
			
			chart.put(d.toString(),total- done);
		}
		
		Map<String,Object>result=new HashMap<>();
		result.put("sprintId", sprintId);
		result.put("burnDown",chart );
		
		return result;
		
	}
	
	
	public Map<String,Object>velocity(Long projectId){
		
		List<Sprint>completed= sprintRepo.findByProjectId(projectId).stream()
				                   .filter(s->s.getState()==SprintState.COMPLETE).collect(Collectors.toList());
		
		Map<String,Integer>velocity=new LinkedHashMap<>();
		
		for(Sprint s:completed) {
			int done= (int)issueRepo.findBySprintId(s.getId()).stream()
					         .filter(i->i.getIssueStatus()==IssueStatus.DONE).count();
			
			velocity.put(s.getSprintName(),done );
		}
		
		Map<String,Object>result= new HashMap<>();
		
		result.put("projectId", projectId);
		result.put("velocity", velocity);
		
		return result;
		
		
	}
	
	public Map<String,Object>sprintReport(Long sprintId){
		
		List<Issue>issues=issueRepo.findBySprintId(sprintId);
		
		long completedIssue= issues.stream().filter(i-> "DONE".equals(i.getIssueStatus().name())).count();
		long notCompetedIssue=issues.size()-completedIssue;
		
		Map<String,Object>result= new HashMap<>();
		result.put("TotalIssues", issues.size());
		result.put("CompletedIssue", completedIssue);
		result.put("Not CompletedIssue", notCompetedIssue);
		
		return result;
		
	}
	
	public Map<String,Object>epicProgessReport(Long epicId){
		
		List<Issue>stories= issueRepo.findByEpicId(epicId);
		
		long done= stories.stream().filter(i-> "DONE".equals(i.getIssueStatus().name())).count();
		
		Map<String,Object>result= new HashMap<>();
		result.put("epicId", epicId);
		result.put("TotalStories", stories.size());
		result.put("completed Stories", done);
		result.put("Progress Percent", stories.isEmpty()? 0: (done*100/stories.size()));
		
		return result;
		
	}
	
	
	public Map<String,Object>cumulativeFlow(Long sprintId){
		List<Issue>issues= issueRepo.findBySprintId(sprintId);
		
		Map<Object, Long> cfd= issues.stream().collect(Collectors.groupingBy(i-> i.getIssueStatus()!=null ? 
				                                                                 i.getIssueStatus():"UNKNOWN",Collectors.counting()));
		
		Map<String,Object>result= new HashMap<>();
		result.put("CumulativeFlow", cfd);
		
		return result;
	}
	
	public Map<String,Object>workLodDistribution(Long sprintId){
		
		List<Issue>issues= issueRepo.findBySprintId(sprintId);
		
		Map<String,Long> load= issues.stream().collect(Collectors.groupingBy(i-> i.getAssigneeEmail() !=null ? 
				                                                            i.getAssigneeEmail():"UNASSIGNED",Collectors.counting()));
		
		
		Map<String,Object>result = new HashMap<>();
		result.put("workload",load );
		return result;
		
		
	}
	
}


