//
//  FacebookService.swift
//  Ennui
//
//  Created by Markus Geilehner on 08.08.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import Foundation

class FacebookService{
    func createFacebookButton(view: UIViewController, delegateClass: FBSDKLoginButtonDelegate,cg: CGRect){
        let loginButton: FBSDKLoginButton = FBSDKLoginButton()
        view.view.addSubview(loginButton)
        loginButton.center = view.view.center;
        loginButton.readPermissions = ["public_profile","email","user_events","user_friends","user_likes"];
        loginButton.delegate = delegateClass;
        loginButton.frame = cg;
        loginButton.titleLabel?.font = UIFont(name: (loginButton.titleLabel?.font.fontName)!, size: 20)
    }
}
