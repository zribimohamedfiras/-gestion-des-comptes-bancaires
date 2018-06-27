/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author firas
 */
public class Personne_physique extends Titulaire_compte {
    
    private ArrayList<Integer> CIN ;
    private int pointsFidelite;
    
    public Personne_physique(int CIN1,int CIN2,String nomTitulaire,String adressTitulaire,int pointsFidelite)
    {
        super(nomTitulaire,adressTitulaire);
        this.CIN= new ArrayList<Integer>();
        this.CIN.add(CIN1);
        if(CIN2!=0)
        {
            this.CIN.add(CIN2);
        }
        this.pointsFidelite=pointsFidelite;
    }
    
    
    
    public Personne_physique()
    {
        CIN= new ArrayList<Integer>();
    }
    
    public void setCIN(ArrayList<Integer> CIN)
    {
        this.CIN=CIN;
    }
    
    public ArrayList<Integer> getCIN()
    {
        return this.CIN;
    }
    
    public void addCIN(int CIN)
    {
        if(this.CIN.size()<2)
        {
            this.CIN.add(CIN);
        }
        else
        {
            System.out.println("On ne peut pas ajouter plus que deux personnes comme Titulaire de compte");
        }
    }
    
    public void setPointsFidelite(int pointsFidelite)
    {
        this.pointsFidelite=pointsFidelite;
    }
    
    public int getPointsFidelite()
    {
        return this.pointsFidelite;
    }
    
    public void afficher()
    {
        super.afficher();
        for(int i =0;i<this.CIN.size();i++)
        {
            System.out.print(", CIN : "+this.CIN.get(i));
        }
        System.out.println(", Points de fidelité : "+this.pointsFidelite);
        
    }
    
    public Personne_physique creerTitulaire()
    {
        Scanner sc= new Scanner(System.in);
        super.creerTitulaireprinc();
        int choixNB;
        do
        {
            System.out.println("entrez le nombre de personne de ce compte (1 ou 2)");
            choixNB= sc.nextInt();
            
        }while((choixNB<1) || (choixNB>2));
        
        for(int i=0;i<choixNB;i++)
        {
            System.out.println("entrez le CIN de personne "+(i+1));
            this.CIN.add(sc.nextInt());
        }
        System.out.println("entrez les points de fidelite");
        this.pointsFidelite= sc.nextInt();
        return this;
        
        
    }
    
    public void ajoutRecompense(int fid,DBCollection coll_personne_physique)
    {
        this.setPointsFidelite(this.getPointsFidelite()+fid);
        //update document
        BasicDBObject updateDocument = new BasicDBObject();
        //new value 
        updateDocument.append("$set", new BasicDBObject()
        .append("pointsFidelite",this.pointsFidelite)
        );
        //old document
        BasicDBObject oldDocument = new BasicDBObject().append("CIN1", this.CIN.get(0));
                    
        coll_personne_physique.update(oldDocument, updateDocument,false,true);
                    
    }
    
    
}
