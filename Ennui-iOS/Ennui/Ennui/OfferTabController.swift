//
//  OfferTabController.swift
//  Ennui
//
//  Created by Markus Geilehner on 04.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import UserNotifications

class OfferTabController: UITableViewController {
    
    var activities: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.barTintColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        let titleDict: NSDictionary = [NSAttributedStringKey.foregroundColor: UIColor.white]
        self.navigationController?.navigationBar.titleTextAttributes = titleDict as? [NSAttributedStringKey : Any]
        self.navigationController?.navigationBar.isTranslucent = false;
        if #available(iOS 11.0, *) {
            self.navigationController?.navigationBar.prefersLargeTitles = true
            self.navigationController?.navigationBar.largeTitleTextAttributes = titleDict as? [NSAttributedStringKey: Any]
        }
        self.tableView.tableFooterView = UIView()
        
        let backend = BackendService()
        backend.doGet(path: "/offers/categories", token: "", callback: {
            (jsonstring) in
            let jsonDecoder = JSONDecoder()
            let jsonData = jsonstring.data(using: .utf8)!
            if let acts:[String] = try? jsonDecoder.decode([String].self, from: jsonData){
                for act in acts{
                    self.activities.append(backend.convertOfferCategoryToName(cat: act))
                }
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }
        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return activities.count
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Activities (" + String(activities.count) + ")"
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "activityCell", for: indexPath)

        cell.textLabel?.text = activities[indexPath.row]
        cell.accessoryType = .disclosureIndicator

        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? OffersListController{
            if let cell = sender as? UITableViewCell{
                dest.activity = cell.textLabel?.text
            }
        }
    }

}
