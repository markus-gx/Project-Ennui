//
//  OfferReviewController.swift
//  Ennui
//
//  Created by Markus Geilehner on 13.10.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class OfferReviewController: UITableViewController {

    var reviews: [Review]!
    var reviewProfileImages: [Int:UIImage] = [:]
    var readMoreCells:[Int:Bool] = [:]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = NSLocalizedString("REVIEWS", comment: "")
        for (idx,_) in reviews.enumerated(){
            readMoreCells[idx] = false
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return self.reviews.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reviewCell", for: indexPath) as! ReviewCell
        let review = self.reviews[indexPath.row]
        if review.authorName == nil{
            self.reviews[indexPath.row].authorName = NSLocalizedString("ANONYMOUS", comment: "")
            cell.authorLabel.text = NSLocalizedString("ANONYMOUS", comment: "")
        }
        else{
            cell.authorLabel.text = review.authorName
        }
        cell.ratingView.rating = Double(review.rating)
        cell.reviewTextLabel.text = review.text
        
        if(self.reviewProfileImages[indexPath.row] == nil){
            cell.profileImage.image = nil
            downloadImageAndSaveForKey(key: indexPath.row, url: review.profile_photo_url);
        }
        else{
            cell.profileImage.image = self.reviewProfileImages[indexPath.row]!;
        }
        cell.readMoreBtn.tag = indexPath.row
        if readMoreCells[indexPath.row]! {
            cell.reviewTextLabel.numberOfLines = 0
            cell.reviewTextLabel.lineBreakMode = NSLineBreakMode.byWordWrapping
            cell.readMoreBtn.setTitle(NSLocalizedString("READ_LESS", comment: ""), for: .normal)
        }
        else{
            cell.reviewTextLabel.numberOfLines = 2
            cell.reviewTextLabel.lineBreakMode = NSLineBreakMode.byTruncatingTail
            cell.readMoreBtn.setTitle(NSLocalizedString("READ_MORE", comment: ""), for: .normal)
        }
        if countLabelLines(label: cell.reviewTextLabel) < 3{
            cell.readMoreBtn.isHidden = true
        }
        else{
            cell.readMoreBtn.isHidden = false
        }
        return cell
    }
 
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if self.readMoreCells[indexPath.row]! {
            return UITableViewAutomaticDimension
        }
        return 165
    }

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    
    @IBAction func readMoreClicked(_ sender: UIButton) {
        if sender.titleLabel?.text == NSLocalizedString("READ_MORE", comment: ""){
            sender.setTitle(NSLocalizedString("READ_LESS", comment: ""), for: .normal)
            self.readMoreCells[sender.tag] = true
        }
        else{
            sender.setTitle(NSLocalizedString("READ_MORE", comment: ""), for: .normal)
            self.readMoreCells[sender.tag] = false
        }
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    func countLabelLines(label: UILabel) -> Int {
        // Call self.layoutIfNeeded() if your view uses auto layout
        let myText = label.text! as NSString
        
        let rect = CGSize(width: label.bounds.width, height: CGFloat.greatestFiniteMagnitude)
        let labelSize = myText.boundingRect(with: rect, options: .usesLineFragmentOrigin, attributes: [NSAttributedStringKey.font: label.font], context: nil)
        
        return Int(ceil(CGFloat(labelSize.height) / label.font.lineHeight))
    }
    
    func downloadImageAndSaveForKey(key: Int, url: String){
        let catPictureURL = URL(string: url)!
        let session = URLSession(configuration: .default)
        let downloadPicTask = session.dataTask(with: catPictureURL) { (data, response, error) in
            if let e = error {
                print("Error downloading cat picture: \(e)")
            } else {
                if (response as? HTTPURLResponse) != nil {
                    if let imageData = data {
                        self.reviewProfileImages[key] = UIImage(data: imageData)
                        DispatchQueue.main.async {
                            self.tableView.reloadData()
                        }
                    } else {
                        print("Couldn't get image: Image is nil")
                    }
                } else {
                    print("Couldn't get response code for some reason")
                }
            }
        }
        downloadPicTask.resume()
    }

}

