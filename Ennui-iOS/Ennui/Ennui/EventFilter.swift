//
//  EventFilter.swift
//  Ennui
//
//  Created by Markus Geilehner on 23.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

class EventFilter{
    var longitude:Double!;
    var latitude:Double!;
    var country:String!;
    var radius:Int32!;
    var startTime:DateComponents!;
    var endTime:DateComponents!;
    var categories:[EventCategories]!;
    var formattedAdress: String!;
    
    init() {
        reset()
    }
    
    func reset(){
        self.longitude = 0.0;
        self.latitude = 0.0;
        self.country = "";
        self.radius = 25;
        initStartTime(year: 0, month: 0, day: 0, hour: 0, minute: 0);
        initEndTime(year: 0, month: 0, day: 0, hour: 0, minute: 0);
        categories = [];
        formattedAdress = "";
    }
    
    func getFilterAsRequestParameters() -> String{
       // "list<string> categories; startTime; endTime; country; double longitude; double latitude; int radius"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EE MMM dd yyyy HH:mm:ss ZZZZ";
        var retVal = String("?longitude=");
        retVal += String(self.longitude);
        retVal += String("&latitude=");
        retVal += String(latitude);
        retVal += String("&country=");
        retVal += String(self.country);
        retVal += String("&radius=");
        retVal += String(self.radius);
        retVal += String("&startTime=");
        retVal += String(dateFormatter.string(from: convertDateComponentToDate(comp: self.startTime)));
        retVal += String("&endTime=");
        retVal += String(dateFormatter.string(from: convertDateComponentToDate(comp: self.endTime)));
        retVal += String("&categories=");
        if self.categories.count > 0{
            for cat in self.categories {
                if cat == self.categories.last{
                    retVal += cat.rawValue
                }
                else{
                    retVal += cat.rawValue + ","
                }
            }
        }
        return retVal;
    }
    
    func initStartTime(year: Int, month: Int, day: Int, hour: Int, minute: Int){
        self.startTime = DateComponents();
        self.startTime.calendar = Calendar.current;
        self.startTime.year = year;
        self.startTime.month = month;
        self.startTime.day = day;
        self.startTime.hour = hour;
        self.startTime.minute = minute;
        self.startTime.second = 0;
    }
    
    func initEndTime(year: Int, month: Int, day: Int, hour: Int, minute: Int){
        self.endTime = DateComponents();
        self.endTime.calendar = Calendar.current;
        self.endTime.year = year;
        self.endTime.month = month;
        self.endTime.day = day;
        self.endTime.hour = hour;
        self.endTime.minute = minute;
        self.endTime.second = 0;
    }
    
    func convertDateComponentToDate(comp: DateComponents) -> Date{
        return NSCalendar.current.date(from: comp)!;
    }
}
