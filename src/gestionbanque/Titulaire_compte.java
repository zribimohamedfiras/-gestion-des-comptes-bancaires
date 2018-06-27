/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import java.util.Scanner;

/**
 *
 * @author firas
 */
public abstract class  Titulaire_compte {
   private String nomTitulaire,adressTitulaire;
   
   public Titulaire_compte(String nomTitulaire,String adressTitulaire)
   {
       this.nomTitulaire=nomTitulaire;
       this.adressTitulaire=adressTitulaire;
   }
   
   public Titulaire_compte()
   {
       
   }
   
   public void setNomTitulaire(String nomTitulaire)
   {
       this.nomTitulaire=nomTitulaire;
   }
   public String getNomTitulaire()
   {
       return this.nomTitulaire;
   }
   
   public void setAdressTitulaire(String adressTitulaire)
   {
       this.adressTitulaire=adressTitulaire;
   }
   public String getAdressTitulaire()
   {
       return this.adressTitulaire;
   }
   
   public void afficher()
   {
       System.out.print("Nom titulaire : "+this.nomTitulaire+", adresse de tiulaire "+this.adressTitulaire);
   }
   
   public void creerTitulaireprinc()
   {
       Scanner sc = new Scanner(System.in);
       System.out.println("entrer le nom de titulaire");
       this.nomTitulaire=sc.next();
       System.out.println("entrer l'adresse de titulaire");
       this.adressTitulaire=sc.next();
       
   }
   
   
   
}
