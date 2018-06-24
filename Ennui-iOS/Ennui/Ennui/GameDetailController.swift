//
//  GameDetailController.swift
//  Ennui
//
//  Created by Markus Geilehner on 03.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class GameDetailController: UIViewController {
    
    var gameDto: GameDto!
    var coverUIImage: UIImage!
    
    @IBOutlet weak var coverImage: UIImageView!
    @IBOutlet weak var descInstLabel: UILabel!
    @IBOutlet weak var gameTitleLabel: UILabel!
    @IBOutlet weak var playerLabel: UILabel!
    
    @IBOutlet weak var ratingView: CosmosView!
    
    
    override func viewWillAppear(_ animated: Bool) {
        navigationItem.title = gameDto.name
        if coverUIImage != nil{
            coverImage.image = coverUIImage
        }
        else{
            if gameDto.cover != nil{
                downloadImageAndSetCover(url: gameDto.cover)
            }
        }
        descInstLabel.text = self.gameDto.description
        gameTitleLabel.text = self.gameDto.name
        playerLabel.text = String(self.gameDto.minPlayer) +  " - " + String(self.gameDto.maxPlayer) + " Players"
        
        if(self.coverImage.subviews.count == 0){
            let overlay: UIView = UIView.init(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: coverImage.frame.size.height))
            overlay.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.4)
            self.coverImage.addSubview(overlay)
        }
        ratingView.rating = Double(gameDto.rating)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func segmentChanged(_ sender: UISegmentedControl) {
        if sender.selectedSegmentIndex == 0{
            descInstLabel.text = gameDto.description
        }
        else if sender.selectedSegmentIndex == 1{
            descInstLabel.text = gameDto.instruction
        }
    }
    
    func downloadImageAndSetCover(url: String){
        let catPictureURL = URL(string: url)!
        let session = URLSession(configuration: .default)
        let downloadPicTask = session.dataTask(with: catPictureURL) { (data, response, error) in
            if let e = error {
                print("Error downloading cat picture: \(e)")
            } else {
                if (response as? HTTPURLResponse) != nil {
                    if let imageData = data {
                        DispatchQueue.main.async {
                            self.coverImage.image = UIImage(data: imageData)
                            self.coverUIImage = UIImage(data: imageData)
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
