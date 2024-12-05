package com.attendwisepro.api;

import com.attendwisepro.models.ApiResponse;
import com.attendwisepro.models.AttendanceRecord;
import com.attendwisepro.models.QRData;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AttendanceService {
    @POST("attendance/record")
    Call<ApiResponse<AttendanceRecord>> recordAttendance(@Body AttendanceRecord record);

    @GET("attendance/learner/{learnerId}")
    Call<ApiResponse<List<AttendanceRecord>>> getLearnerAttendance(
        @Path("learnerId") String learnerId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    @GET("attendance/class/{className}")
    Call<ApiResponse<List<AttendanceRecord>>> getClassAttendance(
        @Path("className") String className,
        @Query("date") String date
    );

    @GET("attendance/stats/learner/{learnerId}")
    Call<ApiResponse<AttendanceStats>> getLearnerStats(
        @Path("learnerId") String learnerId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    @GET("attendance/stats/class/{className}")
    Call<ApiResponse<AttendanceStats>> getClassStats(
        @Path("className") String className,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    public static class AttendanceStats {
        private int totalDays;
        private int daysPresent;
        private int daysLate;
        private int daysAbsent;
        private double attendancePercentage;

        // Getters and Setters
        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

        public int getDaysPresent() { return daysPresent; }
        public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }

        public int getDaysLate() { return daysLate; }
        public void setDaysLate(int daysLate) { this.daysLate = daysLate; }

        public int getDaysAbsent() { return daysAbsent; }
        public void setDaysAbsent(int daysAbsent) { this.daysAbsent = daysAbsent; }

        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { 
            this.attendancePercentage = attendancePercentage; 
        }
    }
}
