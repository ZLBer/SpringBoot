package com.example.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Department;
import com.example.demo.entity.Meeting;
import com.example.demo.service.seatService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by libin on 2019/3/21.
 */
@Controller
@RequestMapping("/demo")
public class HappinessController {

   @Autowired
   private seatService seatService;
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    @RequestMapping("/pre")
    public String pre(Model model){
       List<Meeting> meetings= seatService.findAllMeeting();
        model.addAttribute("meetings",meetings);
        return "pre";
    }
    @RequestMapping("/preAddMeeting")
    public String preAddMeeting(){
        return "addMeeting";
    }

    @RequestMapping("/query")
    @ResponseBody
    public JSONArray testQuery( String meetingId){
        System.out.println(meetingId);
        Meeting meeting =seatService.selectMeetingByID(Integer.parseInt(meetingId));
        JSONArray jsArr = new JSONArray();
        String seats= meeting.getMeetingSeats();
        if(seats==null) return jsArr;
        else
        for(String seat: meeting.getMeetingSeats().split(",")){
            jsArr.add(seat);
        }
        return jsArr;
    }
    @RequestMapping("/queryDepartment")
    @ResponseBody
    public JSONArray queryDepartment( Integer meetingId,String department){
        System.out.println(meetingId+department);
        List<Department> departments =seatService.selectDepartment(meetingId,department);
        JSONArray jsArr = new JSONArray();
         for(Department department1:departments){
             JSONArray jsonArray=  JSONObject.parseArray(department1.getDepartmentSeats());
             for(Object j:jsonArray){
                 jsArr.add(j);
             }
         }
        return jsArr;
    }

    @ResponseBody
    @RequestMapping("/insert")
    public String  testInsert(Integer meetingId,Integer number,String departmentName,@RequestBody String data){
//        System.out.println(meetingId);
//        System.out.println(data);
        JSONArray jsArr = JSONObject.parseArray(data);
        seatService.addSeatToDepartment(number,departmentName,meetingId,data);
//        for(Object j:jsArr){
//            System.out.println(j+",");
//        }
        System.out.println(data);
       seatService.addSeatToMeeting(meetingId,data);
        return "success";
    }
    @RequestMapping("/addMeeting")
    public String addMeeting(@ModelAttribute(value = "meeting") Meeting meeting, Model model){
       int    meetingID=    seatService.addMeeting(meeting.getTitle());
//        System.out.println("test"+meetingID);
//              model.addAttribute("meetingId",meetingID);
//              model.addAttribute("title",meeting.getTitle());
//              return "index";
        List<Meeting> meetings= seatService.findAllMeeting();
        model.addAttribute("meetings",meetings);
        return "pre";
    }
    @RequestMapping("/editMeeting")
    public String editMeeting(String meetingId,Model model){
        Meeting meeting =seatService.selectMeetingByID(Integer.parseInt(meetingId));
        model.addAttribute("meetingId",meeting.getMeetingId());
        model.addAttribute("title",meeting.getTitle());
        return "index";
    }
    @RequestMapping("/delete")
    public String delete(Integer meetingId, Model model) {

        seatService.deleteMeeting(meetingId);
        seatService.deleteMeetingInDepartment(meetingId);
        List<Meeting> meetings = seatService.findAllMeeting();
        model.addAttribute("meetings", meetings);
        return "pre";
    }
    @RequestMapping("/deleteSeat")
    @ResponseBody
    public JSONArray deleteSeat(Integer meetingId, String departmentName) {

        return  seatService.deleteSeat(meetingId,departmentName);

    }
    @RequestMapping("/download")
    public String download(Integer meetingId, String title, Model model, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "attachment;filename=download.doc");
        response.setHeader("content-type","application/msword");
        List<Department>  departments=   seatService.findDepartmentByID(meetingId);

        Map<String,Department> map=new HashMap<>();

        for(Department department:departments){
            StringBuilder sb=new StringBuilder();
            Set<String> area=new HashSet<>();
            JSONArray jsonArray=JSONObject.parseArray(department.getDepartmentSeats());
            //构建seat和erea
            for(Object o:jsonArray){
                String s=(String) o;
               sb.append(seatService.replaceSeat(s));
              area.add(seatService.judgeArea(s));
            }
            //为了合并相同部门的座位
            if(map.containsKey(department.getName())){
                Department department1=map.get(department.getName());
                department1.setNumber(department1.getNumber()+department.getNumber());
               department1.setDepartmentSeats(department1.getDepartmentSeats()+sb.toString());
               String[]temp= department1.getArea().split("；");
               for(String s:temp){
                   area.add(s+"；");
               }
               StringBuilder stringBuilder=new StringBuilder();
               for(String s:area){
                   stringBuilder.append(s);
               }
              department1.setArea(stringBuilder.toString());
              map.put(department.getName(), department1);
            }else {
                department.setDepartmentSeats(sb.toString());
                StringBuilder stringBuilder=new StringBuilder();
                for(String s:area){
                    stringBuilder.append(s);
                }
                department.setArea(stringBuilder.toString());
                map.put(department.getName(),department);
            }
        }
        //将set集合转成list
        List<Department> result=new LinkedList<>();
        List<String> area=new LinkedList<>();
        for(Department department : map.values()){
              result.add(department);
        }
        model.addAttribute("departments", result);
        model.addAttribute("title", title);
        return "download.xml";
    }



}

