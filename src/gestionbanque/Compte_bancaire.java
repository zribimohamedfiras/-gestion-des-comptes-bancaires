/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author firas
 */
public abstract class Compte_bancaire {
    
    private String iban,type,dateCreation,dateMAJ,dateDernierVersement,etat;
    private Titulaire_compte titulaire;
    private double solde,decouvertAutoriser;
    
    public Compte_bancaire(String iban,String type,String etat,Titulaire_compte titulaire,double solde,double decouvertAutoriser,String dateCreation,String dateMAJ,String dateDernierVersement)
    {
        this.iban=iban;
        this.type=type;
        this.titulaire=titulaire;
        this.solde=solde;
        this.decouvertAutoriser=decouvertAutoriser;
        this.etat=etat;
        this.dateCreation=dateCreation;
        this.dateDernierVersement=dateDernierVersement;
        this.dateMAJ=dateMAJ;
        
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String late;
        
        LocalDate today = LocalDate.now();
        String day,now ;
        
        late = LocalTime.now().format(formatter);
        day =  today.toString();
        now  = day+ " "+late;
        this.dateCreation=now;
        this.dateMAJ=now;
        */
    }
    
    public Compte_bancaire()
    {
        
    }
    
    public void afficher()
    {
        System.out.print("IBAN: "+this.iban+", Titulaire: ");
        this.titulaire.afficher();
        System.out.print(" type: "+this.type+", etat: "+this.etat+", solde: "+this.solde+", Decouvert Autoriser: "+this.decouvertAutoriser+", Date de creation: "+this.dateCreation+", Date de dernier mise à jour: "+this.dateMAJ+", Date de dernier version: "+this.dateDernierVersement);
    }

    /**
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * @param iban the iban to set
     */
    public void setIban(String iban) {
        this.iban = iban;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the titulaire
     */
    public Titulaire_compte getTitulaire() {
        return titulaire;
    }

    /**
     * @param titulaire the titulaire to set
     */
    public void setTitulaire(Titulaire_compte titulaire) {
        this.titulaire = titulaire;
    }

    /**
     * @return the solde
     */
    public double getSolde() {
        return solde;
    }

    /**
     * @param solde the solde to set
     */
    public void setSolde(double solde) {
        this.solde = solde;
    }
    
    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }


    /**
     * @return the decouvertAutoriser
     */
    public double getDecouvertAutoriser() {
        return decouvertAutoriser;
    }

    /**
     * @param decouvertAutoriser the decouvertAutoriser to set
     */
    public void setDecouvertAutoriser(double decouvertAutoriser) {
        this.decouvertAutoriser = decouvertAutoriser;
    }
    
    public String getDateCreation() {
        return dateCreation;
    }

    public String getDateMAJ() {
        return dateMAJ;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateMAJ(String dateMAJ) {
        this.dateMAJ = dateMAJ;
    }
    
      public String getDateDernierVersement() {
        return dateDernierVersement;
    }

    public void setDateDernierVersement(String dateDernierVersement) {
        this.dateDernierVersement = dateDernierVersement;
    }

    
    
    public void creerCompte(String code,Titulaire_compte titulaire)
    {
        Scanner sc = new Scanner(System.in);
        this.iban=code;
        String ibn;
        boolean test=false;
        int tt,type,etat;
        do
        {
            System.out.println("entrer le reste de numero du iban");
            System.out.print(code);
            ibn = sc.next();
            try
            {
                for(int i=0;i<ibn.length();i+=5)
                {
                    if(i+5<=ibn.length()-1)
                    {
                        tt=Integer.parseInt(ibn.substring(i, i+5));
                    }
                    else
                    {
                        tt=Integer.parseInt(ibn.substring(i, ibn.length()-1));
                    }
                    
                }
                test=true;
                
            }catch(NumberFormatException e)
            {
                System.out.println("il faut ecrire des chiffres");
                test=false;
            }
            
        }while((test==false)||(ibn.length()!=17));
        
        this.iban+=ibn;
        
        do
        {
            System.out.println("entrer le type de compte (1-courant,2-epargne)");
            type=sc.nextInt();
            
        }while((type<1)||(type>2));
        
        if(type==1)
        {
            this.type="courant";
        }
        else
        {
            this.type="epargne";
        }
        
        do
        {
            System.out.println("entrer l'etat de compte (1-normal,2-bloquer)");
            etat=sc.nextInt();
            
        }while((etat<1)||(etat>2));
        
        if(etat==1)
        {
            this.setEtat("normal");
        }
        else
        {
            this.setEtat("bloquer");
        }
        
        
        this.titulaire=titulaire;
        
        System.out.println("entrer le solde de ce compte");
        this.solde=sc.nextDouble();
        System.out.println("entrer le decouvert Autorisé");
        this.decouvertAutoriser=sc.nextDouble();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String late;
        
        LocalDate today = LocalDate.now();
        String day,now ;
        
        late = LocalTime.now().format(formatter);
        day =  today.toString();
        now  = day+ " "+late;
        this.dateCreation=now;
        this.dateMAJ=now;
        this.dateDernierVersement=now;
    }

  

    
    
}
