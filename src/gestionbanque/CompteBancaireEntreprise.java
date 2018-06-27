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
public class CompteBancaireEntreprise extends Compte_bancaire {
    private String regimeFiscale;
    
    public CompteBancaireEntreprise(String iban,String type,String etat,Titulaire_compte titulaire,double solde,double decouvertAutoriser,String regimeFiscale,String dateCreation,String dateMAJ,String dateDernierVersement)
    {
        super(iban,type,etat,titulaire,solde,decouvertAutoriser,dateCreation,dateMAJ,dateDernierVersement);
        this.regimeFiscale=regimeFiscale;
                
    }
    
    public CompteBancaireEntreprise()
    {
        
    }
    
    public void afficher()
    {
        super.afficher();
        System.out.println(", Régime fiscale: "+this.regimeFiscale);
    }

    public String getRegimeFiscale() {
        return regimeFiscale;
    }

    public void setRegimeFiscale(String regimeFiscale) {
        this.regimeFiscale = regimeFiscale;
    }
    
    //creation compte bancaire
    public void creerCompte(String code,Titulaire_compte titulaire)
    {
        Scanner sc = new Scanner(System.in);
        super.creerCompte(code, titulaire);
        System.out.println("entrer le regime fiscale");
        this.regimeFiscale=sc.next();
        
    }
    
    
}
