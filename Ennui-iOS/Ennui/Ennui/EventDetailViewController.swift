//
//  EventDetailViewController.swift
//  Ennui
//
//  Created by Markus Geilehner on 24.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class EventDetailViewController: UITableViewController {
    var eventDto: EventDto? = nil;
    var cover: UIImage? = nil;
    @IBOutlet weak var coverImage: UIImageView!
    @IBOutlet weak var timespanLabel: UILabel!
    @IBOutlet weak var byOwnerLabel: UILabel!
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var webLinkButton: UIButton!
    @IBOutlet weak var descriptionLabel: UILabel!
    
    @IBOutlet weak var locationCell: UITableViewCell!
    @IBOutlet weak var taxiCell: UITableViewCell!
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.title = eventDto?.name;
        if(cover == nil && eventDto?.coverUrl != nil){
            downloadImageAndSetCover(url: (eventDto?.coverUrl)!);
        }
        else{
            self.coverImage.image = self.cover;
        }
        self.eventNameLabel.text = self.eventDto?.name;
        if let owner = self.eventDto?.ownerName{
            self.byOwnerLabel.text = "by " + owner;
        }
        else{
            self.byOwnerLabel.text = ""
        }
        
        self.timespanLabel.text = self.eventDto?.getTimeSpanLabel();
        self.webLinkButton.titleLabel?.text = NSLocalizedString("Website (click)", comment: "")
        if(eventDto?.ticketUri == nil || (eventDto?.ticketUri.count)! < 5){
            self.webLinkButton.titleLabel?.text = NSLocalizedString("No Website", comment: "")
        }
        
        self.descriptionLabel.lineBreakMode = .byWordWrapping;
        self.descriptionLabel.numberOfLines = 0;
        self.descriptionLabel.text = eventDto?.description;
        
        if(self.coverImage.subviews.count == 0){
            let overlay: UIView = UIView.init(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: coverImage.frame.size.height))
            overlay.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.4)
            self.coverImage.addSubview(overlay)
        }
        
        
        
        self.locationCell.accessoryType = .disclosureIndicator;
        self.taxiCell.accessoryType = .disclosureIndicator;
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.tableFooterView = UIView()
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func clickWebsite(_ sender: Any) {
        if(eventDto?.ticketUri != nil){
            let url = URL(string: (self.eventDto?.ticketUri)!)
            if url != nil{
                UIApplication.shared.open(url!, options: [:], completionHandler: nil)
            }
        }
    }
    
    func downloadImageAndSetCover(url: String){
        let catPictureURL = URL(string: url) ?? URL(string: "https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg")
        let session = URLSession(configuration: .default)
        let downloadPicTask = session.dataTask(with: catPictureURL!) { (data, response, error) in
            if let e = error {
                print("Error downloading cat picture: \(e)")
            } else {
                if (response as? HTTPURLResponse) != nil {
                    if let imageData = data {
                        DispatchQueue.main.async {
                            self.coverImage.image = UIImage(data: imageData)
                            self.cover = UIImage(data: imageData)
                        }
                    } else {
                        print("Couldn't get image: Image is nil")
                    }
                } else {
                    print("Couldn't get response code for some reason")
                }
            }
            DispatchQueue.main.async {
                self.tableView.reloadData();
            }
        }
        downloadPicTask.resume()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "locationDetailSegue"{
            if let dest = segue.destination as? LocationEventDetailController{
                dest.eventDto = eventDto
            }
        }
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
