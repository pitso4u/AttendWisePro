package com.attendwisepro.network;

import com.attendwisepro.models.LoginRequest;
import com.attendwisepro.models.LoginResponse;
import com.attendwisepro.models.AttendanceResponse;
import com.attendwisepro.models.DashboardData;
import com.attendwisepro.models.Learner;
import com.attendwisepro.models.Employee;
import com.attendwisepro.models.Incident;
import com.attendwisepro.models.Visitor;
import com.attendwisepro.models.ReportResponse;
import com.attendwisepro.models.AttendanceDetail;
import com.attendwisepro.models.CountResponse;

import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Unified API service interface for all network operations in AttendWisePro
 */
public interface ApiService {
    // Authentication endpoints
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    // Dashboard endpoints
    @GET("dashboard")
    Call<DashboardData> getDashboardData(@Header("Authorization") String token);

    // Attendance endpoints
    @POST("attendance")
    Call<ResponseBody> markAttendance(
        @Header("Authorization") String token,
        @Body String requestBody
    );

    @POST("attendance/class")
    Call<ResponseBody> markClassAttendance(
        @Header("Authorization") String token,
        @Body String requestBody
    );

    @GET("attendance/student/{studentId}")
    Call<AttendanceResponse> getStudentAttendance(
        @Header("Authorization") String token,
        @Path("studentId") String studentId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    @POST("attendance")
    Call<ResponseBody> submitAttendance(
        @Header("Authorization") String token,
        @Query("class") String className,
        @Query("date") String date,
        @Body Map<String, Boolean> attendanceMap
    );

    // Student (Learner) management endpoints
    @GET("learners")
    Call<List<Learner>> getLearners(@Header("Authorization") String token);

    @POST("learners")
    Call<Void> saveLearner(
        @Header("Authorization") String token,
        @Body Learner learner
    );

    @GET("learners/search")
    Call<List<Learner>> searchLearners(
        @Header("Authorization") String token,
        @Query("query") String searchQuery
    );

    @DELETE("learners/{learnerId}")
    Call<Void> deleteLearner(
        @Header("Authorization") String token,
        @Path("learnerId") String learnerId
    );

    @GET("learners/class/{className}")
    Call<List<Learner>> getLearnersByClass(
        @Header("Authorization") String token,
        @Path("className") String className
    );

    @GET("learners/count")
    Call<CountResponse> getLearnerCount(@Header("Authorization") String token);

    // Employee endpoints
    @POST("employees")
    Call<ResponseBody> saveEmployee(
        @Header("Authorization") String token,
        @Body Employee employee
    );

    @GET("employees")
    Call<List<Employee>> getEmployees(
        @Header("Authorization") String token
    );

    @GET("employees/count")
    Call<CountResponse> getEmployeeCount(@Header("Authorization") String token);

    // Incident endpoints
    @POST("incidents")
    Call<ResponseBody> createIncident(
        @Header("Authorization") String token,
        @Body Incident incident
    );

    @GET("incidents")
    Call<List<Incident>> getIncidents(
        @Header("Authorization") String token
    );

    // Report endpoints
    @GET("reports")
    Call<ReportResponse> getReport(
        @Header("Authorization") String token,
        @Query("date") String date,
        @Query("class") String className
    );

    // Class endpoints
    @GET("classes")
    Call<List<String>> getClasses(@Header("Authorization") String token);

    // Visitor endpoints
    @POST("visitors")
    Call<ResponseBody> createVisitor(
        @Header("Authorization") String token,
        @Body Visitor visitor
    );
}
