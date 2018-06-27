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
public class Personne_moral extends Titulaire_compte {
    
    private String nomCommercial;
    private int identifiantEntreprise;
    
    public Personne_moral(String nomTitulaire,String adressTitulaire,String nomCommercial,int identifiantEntreprise)
    {
        super(nomTitulaire,adressTitulaire);
        this.nomCommercial=nomCommercial;
        this.identifiantEntreprise=identifiantEntreprise;
    }
    
    public Personne_moral()
    {
        
    }
    
    public void setNomCommercial(String nomCommercial)
    {
        this.nomCommercial=nomCommercial;
    }
    
    public String getNomCommercial()
    {
        return this.nomCommercial;
    }
    
    public void setIdentifiantEntreprise(int identifiantEntreprise)
    {
        this.identifiantEntreprise= identifiantEntreprise;
    }
    
    public int getIdentifiantEntreprise()
    {
        return this.identifiantEntreprise;
    }
    
    public void afficher()
    {
        super.afficher();
        System.out.println(", Nom de commercial : "+this.nomCommercial+", Identifiant d'entreprise : "+this.identifiantEntreprise);
    }
    
    public Personne_moral creerTitulaire()
    {
        Scanner sc= new Scanner(System.in);
        super.creerTitulaireprinc();
        
        System.out.println("entrer le nom commercial de ce nouveau titulaire");
        this.nomCommercial= sc.next();
        
        System.out.println("entrer l'identifiant de l'entreprise");
        this.identifiantEntreprise=sc.nextInt();
        return this;
        
    }
    
}
