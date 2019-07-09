package com.example.demo.controller;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.model.User;
import com.example.demo.repo.AppointmentRepo;
import com.example.demo.repo.DoctorRepo;
import com.example.demo.repo.PatientRepo;
import com.example.demo.repo.UserRepo;


@Controller
public class HospitalController {
	
	private int id;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	AppointmentRepo appointmentRepo;
	
	@Autowired
	DoctorRepo doctorRepo;
	
	@Autowired
	PatientRepo patientRepo;
	
	@GetMapping(value="/dummyDataInput")

	public String testing()
	{	
		
		Patient p1 = new Patient(false, "Janis", "Skaidrais", "Janis", "11111");
		Patient p2 = new Patient(false, "Sandra", "Skaista", "Sandra", "22222");
		Patient p3 = new Patient(false, "Emils", "Garais", "Emils", "33333");
		patientRepo.save(p1);
		patientRepo.save(p1);
		patientRepo.save(p1);
		
		
		Doctor d1 = new Doctor((short)1, "Gints", "Drosais", "Gints", "11111" );
		Doctor d2 = new Doctor((short)2, "Spodra", "Jaunsirde" , "Spodra", "22222");
		Doctor d3 = new Doctor((short)3, "Zigfrids", "Labotajs", "Zigis", "33333" );
		doctorRepo.save(d1);
		doctorRepo.save(d2);
		doctorRepo.save(d3);
		
		
		Calendar calendar  = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        Date now = (Date) calendar.getTime();
		Appointment a1 = new Appointment(now, "Flue", p1, d1);
		calendar.add(Calendar.DATE, 3);
        now = (Date) calendar.getTime();
		Appointment a2 = new Appointment(now, "Hart problems", p2, d2);
		calendar.add(Calendar.DATE, 4);
        now = (Date) calendar.getTime();
		Appointment a3 = new Appointment(now, "lung problem", p3, d3);
		appointmentRepo.save(a1);
		appointmentRepo.save(a2);
		appointmentRepo.save(a3);
		
		
		
		return "showalldoctors"	;
	}
	
	@GetMapping(value="/showAllDoctors")
	public String showAllDoctors(Model model) {
		
		model.addAttribute("doctor", doctorRepo.findAll());
		
		return "showalldoctors";
		
	}
	
	@GetMapping(value="/showAllPatients")
	public String showAllPatients(Model model) {
		
		model.addAttribute("patient", patientRepo.findAll());
		
		return "showallpatients";
		
	}
	@GetMapping(value="/insertNewPatient") 
	public String insertNewPatientGet(Patient patient) {
		
		return "insertnewpatient";
	}
	
	
	@PostMapping(value="/insertNewPatient") 
	public String insertNewPatientPost(Patient patient) {
		
		patientRepo.save(patient);
		
		return "redirect:/showAllPatients";
	}
	
	@GetMapping(value="/insertNewDoctor") 
	public String insertNewDoctorGet(Doctor doctor) {
		
		return "insertnewdoctor";
	}
	
	
	@PostMapping(value="/insertNewDoctor") 
	public String insertNewDoctorPost(Doctor doctor) {
		
		doctorRepo.save(doctor);
		
		return "redirect:/showAllDoctors";
	}
	
	
	@GetMapping(value="/authorise") 
	public String authoriseGet(User user) {
		
		return "authorise";
	}
	
	@PostMapping(value="/authorise")
	public String authorisePost(User user) {

		User u = userRepo.findByUsernameAndPassword(user.getUsername(), user.getPassword());
		if(u!=null) {
			id = u.getId();
			if(u.getDoctor() != null) {
				
				return "redirect:/showAllAppointmentsByDoctor/" + id;
			
			}else{
				
				return "redirtect:/showAllAppointmentsByPatient/" + id ;
			}
			
			
		}
		else {
			return "redirect:/authorise";
		}

	
	}
	
	@GetMapping(value = "/showAllAppointmentsByPatient/{id}")
	public String showAllAppointmentByPatientId(Model model, @PathVariable(name = "id") int id) {
		
		Patient p = userRepo.findById(id).get().getPatient();
		
		ArrayList<Appointment> ap = appointmentRepo.findByPatient(p);
		
		model.addAttribute("app" , ap);
		
		return "showallappoitmentsbyid";
	}
	
	@GetMapping(value = "/showAllAppointmentsByDoctor/{id}")
	public String showAllAppointmentByDoctorId(Model model, @PathVariable(name = "id") int id) {
		
		Doctor d = userRepo.findById(id).get().getDoctor();
		
		ArrayList<Appointment> ap = appointmentRepo.findByDoctor(d);
		
		model.addAttribute("app" , ap);
		
		return "showallappoitmentsbyid";
	}
			
	
	@GetMapping(value="/insertNewAppointment")
	public String insertNewAppoitnmentGet(Model model,Integer doctorId, Integer patientId, String date, String description) {
		
		model.addAttribute("doctor", doctorRepo.findAll());
		model.addAttribute("patient", patientRepo.findAll());
		return "insertnewappointment";
	
	}
	
	@PostMapping(value="/insertNewAppointment")
	public String insertNewAppoitnmentPost(Model model, Integer doctorId, Integer patientId, String date, String description) throws ParseException {
		

		Optional<User> du = userRepo.findById(doctorId);
		
		Optional<User> pu = userRepo.findById(patientId);
		
		
		
		if(du != null && pu != null) {
			System.out.println(doctorId);
		System.out.println(patientId);
			Doctor d = du.get().getDoctor();
			Patient p = pu.get().getPatient();
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date date1 = format.parse(date);
		
		Appointment a = new Appointment(date1, description, p, d);
		
		appointmentRepo.save(a);
		
		return "redirect:/showAllDoctors";
		
		}else {
			return "redirect:/insertNewAppointment";
		}
		
		
		
		
	
	}
	
	
}
