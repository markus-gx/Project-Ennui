//
//  CategoryCollectionView.swift
//  Ennui
//
//  Created by Markus Geilehner on 01.09.17.
//  Copyright © 2017 Markus G. All rights reserved.
//

import UIKit

class CategoryCollectionView: UICollectionView, UICollectionViewDataSource, UICollectionViewDelegate{
    var categories: [String] = []

    var categoriesSelected: [Bool] = [];
    
    override func awakeFromNib() {
        self.delegate = self;
        self.dataSource = self;
        let backend = BackendService()
        categories = backend.eventCategories
        for _ in categories{
            categoriesSelected.append(false)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = dequeueReusableCell(withReuseIdentifier: "categoryCell", for: indexPath) as! CategoryCell
        cell.categoryName.text = categories[indexPath.item]
        if !categoriesSelected[indexPath.item]{
            cell.backgroundColor = UIColor.init(red: 53.0/255.0, green: 70.0/255.0, blue: 90.0/255.0, alpha: 1.0)
        }
        else{
            cell.backgroundColor = UIColor.init(red: 41.0/255.0, green: 145.0/255.0, blue: 228.0/255.0, alpha: 1.0)
        }
        cell.categoryName.textColor = UIColor.white
        cell.layer.cornerRadius = 10;
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return categories.count
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if categoriesSelected[indexPath.item]{
            categoriesSelected[indexPath.item] = false;
        }
        else{
            categoriesSelected[indexPath.item] = true;
        }
        DispatchQueue.main.async {
            collectionView.reloadData()
        }
    }
    
    func getSelectedCategoryNames() -> [String]{
        var arr: [String] = [];
        for (idx,name) in categories.enumerated() {
            if categoriesSelected[idx]{
                arr.append(name)
            }
        }
        return arr
    }
    
    func resetCategoriesSelected(){
        for (idx,_) in categoriesSelected.enumerated(){
            categoriesSelected[idx] = false
        }
    }
    
    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */

}
