//
//  ViewController.swift
//  Ennui
//
//  Created by Markus G. on 30/07/2017.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class ViewController: UIViewController, FBSDKLoginButtonDelegate{

    var strings: [String] = [];
    @IBOutlet weak var bulletPoints: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let fbService = FacebookService();


        if(FBSDKAccessToken.current() != nil){
            doBackendLogin()
        }
        else{
            let cg = CGRect(x: view.frame.width/2 - 150, y: view.frame.height/2 + 50, width: 300, height: 50)
            fbService.createFacebookButton(view: self,delegateClass: self, cg: cg);
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func loginButton(_ loginButton: FBSDKLoginButton!, didCompleteWith result: FBSDKLoginManagerLoginResult!, error: Error!) {
        if(error != nil){
            //error handling
        }
        else if(result.isCancelled){
            //handle cancel
        }
        else{
            doBackendLogin();
        }
    }
    
    func doBackendLogin(){
        let backendService = BackendService()
        backendService.doPost(path: "/users/login", dict: [:],token: FBSDKAccessToken.current().tokenString, callback: { (nsdict) in
            let loginUserDto = backendService.parseLoginJSON(json: nsdict);
            let userDefaults = UserDefaults.standard;
            let encoder = JSONEncoder()
            if let encoded = try? encoder.encode(loginUserDto){
                userDefaults.set(encoded, forKey: "userDto");
            }
            userDefaults.synchronize();
            //Observer Pattern
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "userDataRecieved"), object: nil);
        })
        segueLoginToMain();
    }
    func segueLoginToMain(){
        DispatchQueue.main.async(){
            self.performSegue(withIdentifier: "mainViewToTabView", sender: self)
        }
    }
    
    func loginButtonDidLogOut(_ loginButton: FBSDKLoginButton!) {
        //logged out
        print("logged out");
    }

}

