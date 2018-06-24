//
//  EventDto.swift
//  Ennui
//
//  Created by Markus Geilehner on 23.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

struct EventDto: Codable{
    var id: Int!;
    var eventId: Int!;
    var endtime: Date!;
    var starttime: Date!;
    var name: String!;
    var description: String!;
    var placeName: String!;
    var country: String!;
    var city: String!;
    var latitude: Double!;
    var longitude: Double!;
    var street: String!;
    var zip: String!;
    var ownerName: String!;
    var ownerId: Int!;
    var favored: Bool!;
    var coverUrl: String!;
    var ticketUri: String!;
    var activated: Bool!;
    var category: EventCategories;
    
    init() {
        category = EventCategories.OTHERS
    }
    
    func getTimeSpanLabel() -> String{
        if self.starttime != nil && self.endtime != nil{
            let formatter = DateFormatter()
            formatter.dateFormat = "dd-MM-YYYY HH:mm"
            formatter.timeZone = TimeZone(abbreviation: "GMT");
            
            return formatter.string(from: starttime) + " - " + formatter.string(from: endtime);
        }
        return "No Time provided!"
    }
}
