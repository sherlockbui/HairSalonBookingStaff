package com.example.hairsalonbookingstaff.Interface;


import com.example.hairsalonbookingstaff.Model.BookingInfomation;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<BookingInfomation> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
