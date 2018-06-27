/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientURI;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;
import org.jocl.CL;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

/**
 *
 * @author firas
 */
public class GestionBanque {

    
    private static String tauxkernel =
        "__kernel void "+
        "sampleKernel(__global const float *a,"+
        "             __global const float *b,"+
        "             __global float *c)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    c[gid] = a[gid]+(a[gid]*(b[0]/100));"+
        "}";
    
    public static ArrayList<Double> calcultaux(float taux,Object[] soldeListe)
    {
        int n = soldeListe.length;
        
        float dstArray[] = new float[n];
        float[] tabbonus = new float[n];
        float[] tauxI = new float[1];
        double[] tauxf= new double[n];
        tauxI[0]=taux;
        
        
        for(int i = 0;i<n;i++)
        {
            tauxf[i]= (double) soldeListe[i];
        }
        
        for(int i = 0;i<n;i++)
        {
            tabbonus[i]= (float) tauxf[i];
        }
        
        
        Pointer srcA = Pointer.to(tabbonus);
        Pointer srcB = Pointer.to(tauxI);
        Pointer dst = Pointer.to(dstArray);
        
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        
        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        
        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        cl_command_queue commandQueue = 
            clCreateCommandQueue(context, device, 0, null);
        
        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcA, null);
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_int * 1, srcB, null);
        memObjects[2] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n, null, null);
        
        
        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ tauxkernel }, null, null);
        
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        
        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0, 
            Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1, 
            Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, 
            Sizeof.cl_mem, Pointer.to(memObjects[2]));
        
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, local_work_size, 0, null, null);
        
        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);
        
        // Release kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        //return soldes
        ArrayList<Double> res = new ArrayList<Double>();
        for(int i=0;i<dstArray.length;i++)
        {
            res.add((double)dstArray[i]);
        }
        
        return res;
        
    }
    
    
    public static void remain()
    {
        String uri="mongodb://campos:campos@testjavamgdb-shard-00-00-yzq2r.mongodb.net:27017,testjavamgdb-shard-00-01-yzq2r.mongodb.net:27017,testjavamgdb-shard-00-02-yzq2r.mongodb.net:27017/admin?replicaSet=testjavamgdb-shard-0&ssl=true";
        MongoClientURI clientURI = new MongoClientURI(uri); 
        MongoClient mongoClient = new MongoClient(clientURI);
        
        //Connecting with database
        DB dbs = mongoClient.getDB("testmongoDB");
        //System.out.println("Connect to database successfully");
        
        //create collection
        DBCollection coll_banque = dbs.getCollection("Banque");
        DBCollection coll_compte_particulier = dbs.getCollection("compte_particulier");
        DBCollection coll_compte_entreprise = dbs.getCollection("compte_entreprise");
        DBCollection coll_personne_physique = dbs.getCollection("personne_physique");
        DBCollection coll_personne_moral = dbs.getCollection("personne_moral");
        //System.out.println("Collection create successfully");
        
        Banques allBanque= new Banques();
        
        Cursor cursor;
        
        Scanner sc= new Scanner(System.in);
        int choix_menu,choix_banque;
        String codebanque1;
        boolean testcodebanque;
        Banque espacebanque=null,ban;
        CompteBancaireParticulier cbp;
        CompteBancaireEntreprise cbe;
        try
        {
            //extraire les banques de la BD
            cursor= coll_banque.find();
            ArrayList<DBObject> object_banque=new ArrayList<DBObject>();
            while(cursor.hasNext())
            {
                object_banque.add(cursor.next());
            }
            
            object_banque.stream().forEach(i-> {
                allBanque.addBanque(new Banque((String)i.get("nom"),(String)i.get("code")));
                //System.out.println("nom: "+Banques.getAllBanque().get(0).getNomBanque());
            });
            
            //extraire les personnes physiques de la BD
            cursor= coll_personne_physique.find();
            ArrayList<DBObject> object_physique=new ArrayList<DBObject>();
            while(cursor.hasNext())
            {
                object_physique.add(cursor.next());
            }
            
            object_physique.stream().forEach(i-> {
                
                Banques.getAllBanque().stream().filter(j-> j.getCodeBanque().compareTo((String)i.get("codeBanque"))==0).forEach(k-> {
                    
                    k.getTitulairesP().add(new Personne_physique((int)i.get("CIN1"),(int)i.get("CIN2"),(String) i.get("nomTitulaire"),(String)i.get("adressTitulaire"),(int)i.get("pointsFidelite")));
                    
                });
            });
            
            //Banques.getAllBanque().get(0).getTitulairesP().stream().forEach(i-> i.afficher() );
            
            //extraire les personnes morales de la BD
            cursor= coll_personne_moral.find();
            ArrayList<DBObject> object_moral=new ArrayList<DBObject>();
            while(cursor.hasNext())
            {
                object_moral.add(cursor.next());
            }
            
            object_moral.stream().forEach(i-> {
                Banques.getAllBanque().stream().filter(j-> j.getCodeBanque().compareTo((String)i.get("codeBanque"))==0).forEach(k->{
                    k.getTitulairesM().add(new Personne_moral((String)i.get("nomTitulaire"),(String)i.get("adressTitulaire") , (String)i.get("nomCommercial"), (int)i.get("identifiant")));
                });
                
            });
            //Banques.getAllBanque().get(0).getTitulairesM().stream().forEach(i-> i.afficher() );
            
            
            //prendre les comptes particulière de la db
            cursor= coll_compte_particulier.find();
            ArrayList<DBObject> object_particulier=new ArrayList<DBObject>();
            while(cursor.hasNext())
            {
                object_particulier.add(cursor.next());
            }
            object_particulier.stream().forEach(i-> {
                Banques.getAllBanque().stream().filter(j-> j.getCodeBanque().compareTo((String)i.get("codeBanque"))==0).forEach(k-> {
                    
                    k.getTitulairesP().stream().filter(l-> {
                        AppuiClass.personnecin=(int)i.get("titulaire");
                        return l.getCIN().contains(AppuiClass.personnecin);
                            }).forEach(m-> {
                        k.getComptes().add(new CompteBancaireParticulier((String)i.get("iban"),(String)i.get("type"),(String)i.get("etat"),m,(double)i.get("solde"),(double)i.get("decouvertAutoriser"),(double)i.get("debitMax"),(String)i.get("numeroCarteBancaire"),(String)i.get("dateCreation"),(String)i.get("dateMAJ"),(String)i.get("dateDernierVersement")));
                    });
                    });
            });
            //System.out.println(Banques.getAllBanque().get(0).getComptes().get(0).getTitulaire().getNomTitulaire());
            
            //extraire les comptes entreprise du BD
            cursor= coll_compte_entreprise.find();
            ArrayList<DBObject> object_entreprise=new ArrayList<DBObject>();
            while(cursor.hasNext())
            {
                object_entreprise.add(cursor.next());
            }
            object_entreprise.stream().forEach(i-> {
                Banques.getAllBanque().stream().filter(j-> j.getCodeBanque().compareTo((String)i.get("codeBanque"))==0).forEach(k-> {
                    k.getTitulairesM().stream().filter(l-> {
                        AppuiClass.personnecin=(int) i.get("titulaire");
                        return l.getIdentifiantEntreprise()==AppuiClass.personnecin;
                    }).forEach(m-> {
                        k.getComptes().add(new CompteBancaireEntreprise((String)i.get("iban"),(String)i.get("type"),(String)i.get("etat"),m,(double)i.get("solde"),(double)i.get("decouvertAutoriser"), (String)i.get("regimeFiscale"),(String)i.get("dateCreation"),(String)i.get("dateMAJ"),(String)i.get("dateDernierVersement")));
                    });
                });
            });
            
            for(int i=0;i<Banques.getAllBanque().size();i++)
            {
                
                for(int j=0;j<Banques.getAllBanque().get(i).getComptes().size()-1;j++)
                {
                    for(int k=(j+1);k<Banques.getAllBanque().get(i).getComptes().size();k++)
                    {
                        if(Banques.getAllBanque().get(i).getComptes().get(j).getIban().compareTo(Banques.getAllBanque().get(i).getComptes().get(k).getIban())==0)
                        {
                            Banques.getAllBanque().get(i).getComptes().remove(j);
                            break;
                        }
                    }
                }
            }
            //affichage des comptes
            Banques.getAllBanque().get(0).getComptes().stream().forEach(i-> i.afficher());
            
            int choixCent;
            do
            {
                do
                {
                    System.out.println("1-Acceder à l'espace de banque centrale");
                    System.out.println("2-Acceder à l'espace bancaire");
                    System.out.println("3-Quitter");
                    choix_banque=sc.nextInt();
                    
                }while((choix_banque<1)||(choix_banque>3));
                
                switch(choix_banque)
                {
                    case 1 : {
                                do
                                {
                                    System.out.println("1-Creer Banque");
                                    System.out.println("2-Supprimer Banque");
                                    choixCent=sc.nextInt();

                                }while((choixCent<1)||(choixCent>2));
                                switch (choixCent)
                                {
                                    case 1 : 
                                            {
                                                Banque bn= allBanque.creerBanque();
                                                if(bn==null)
                                                {
                                                    System.out.println("Creation de banque echouée");
                                                }
                                                else
                                                {
                                                    BasicDBObject doc1 = new BasicDBObject("nom",bn.getNomBanque())
                                                    .append("code", bn.getCodeBanque());
                                                    coll_banque.insert(doc1);
                                                    System.out.println("Banque crée avec succée");
                                    
                                                }
                                                break;
                                            }
                                            
                                    case 2 :
                                            {
                                                String codebn;
                                                boolean supprime;
                                                do
                                                {
                                                    System.out.println("entrer le code de banque que vous voulez supprimer");
                                                    codebn=sc.next();
                                                    if(codebn.length()!=7)
                                                    {
                                                        System.out.println("Le code de banque doit contenir 7 charactéres");
                                                    }
                                                    supprime= allBanque.removeBanque(codebn);
                                                    if(!supprime)
                                                    {
                                                        System.out.println("banque introuvable");
                                                    }
                                                }while((codebn.length()!=7)|| !supprime);
                                                if(supprime)
                                                {
                                                    BasicDBObject del = new BasicDBObject("code", codebn);
                                                    coll_banque.remove(del);
                                                    System.out.println("Banque supprimer");
                                                }
                                         break;
                                            }
                                }
                                 break;
                             }
                    case 2 : {
                                testcodebanque=false;
                                do
                                {
                                    System.out.println("entrer le code de votre banque");
                                    codebanque1=sc.next();
                                    for(int i=0;i<Banques.getAllBanque().size();i++)
                                    {
                                        if(Banques.getAllBanque().get(i).getCodeBanque().compareTo(codebanque1)==0)
                                        {
                                            testcodebanque=true;
                                            espacebanque=Banques.getAllBanque().get(i);
                                        }
                                    }
                                        
                                }while(!testcodebanque);
                                    
                                do
                                {
                                    
                                    System.out.println("*************"+espacebanque.getNomBanque()+"*************");
                                    
                                    do
                                    {
                                        System.out.println("1-Créer compte particulière");
                                        System.out.println("2-Créer compte entreprise");
                                        System.out.println("3-Debiter compte");
                                        System.out.println("4-Créditer compte");
                                        System.out.println("5-Virer");
                                        System.out.println("6-Fermer compte");
                                        System.out.println("7-Calculer taux intéret");
                                        System.out.println("8-Recompenser clients fidéles");
                                        System.out.println("9-Quitter");
                                        choix_menu= sc.nextInt();
                
                                    }while((choix_menu<1) || (choix_menu>9));
            
                                    switch (choix_menu)
                                    {
                                        case 1: {
                                                    cbp= espacebanque.creerCompteParticulier();
                                                    Personne_physique pr=(Personne_physique) cbp.getTitulaire();
                                                    
                                                    cursor= coll_personne_physique.find();
                                                    ArrayList<DBObject> per_physique=new ArrayList<DBObject>();
                                                    while(cursor.hasNext())
                                                    {
                                                        per_physique.add(cursor.next());
                                                    }
                                                    AppuiClass.cin.clear();
                                                    per_physique.stream().forEach(i-> AppuiClass.cin.add((int)i.get("CIN1")));
                                                    boolean testper=AppuiClass.cin.stream().anyMatch(i-> i==pr.getCIN().get(0));
                                                    //System.out.println(testper);
                                                    
                                                    if(!testper)
                                                    {
                                                        BasicDBObject doc2 = new BasicDBObject("nomTitulaire",pr.getNomTitulaire())
                                                        .append("adressTitulaire", pr.getAdressTitulaire())
                                                        .append("pointsFidelite", pr.getPointsFidelite())
                                                        .append("codeBanque", espacebanque.getCodeBanque())
                                                        .append("CIN1", pr.getCIN().get(0));
                                                        if(pr.getCIN().size()>1)
                                                        {
                                                            doc2.append("CIN2", pr.getCIN().get(1));
                                                        }
                                                        else
                                                        {
                                                            doc2.append("CIN2", 0);
                                                        }
                                                  
                                                        coll_personne_physique.insert(doc2);
                                                    }
                                                    
                                                    
                                                    BasicDBObject doc1 = new BasicDBObject("iban",cbp.getIban())
                                                    .append("codeBanque", espacebanque.getCodeBanque())
                                                    .append("dateDernierVersement", cbp.getDateDernierVersement())
                                                    .append("dateMAJ", cbp.getDateMAJ())
                                                    .append("debitMax", cbp.getDebitMax())
                                                    .append("decouvertAutoriser", cbp.getDecouvertAutoriser())
                                                    .append("etat", cbp.getEtat())
                                                    .append("numeroCarteBancaire", cbp.getNumeroCarteBancaire()) 
                                                    .append("solde", cbp.getSolde()) 
                                                    .append("type", cbp.getType())
                                                    .append("titulaire", pr.getCIN().get(0))
                                                    .append("dateCreation", cbp.getDateCreation());
                                                    coll_compte_particulier.insert(doc1);
                                                    System.out.println("Operation effectuée avec succée");
                                                    break;
                                                }
                                                
                                        case 2: {
                                                    cbe= espacebanque.creerCompteEntreprise();
                                                    Personne_moral prm=(Personne_moral) cbe.getTitulaire();
                                                    cursor= coll_personne_moral.find();
                                                    ArrayList<DBObject> per_moral=new ArrayList<DBObject>();
                                                    while(cursor.hasNext())
                                                    {
                                                        per_moral.add(cursor.next());
                                                    }
                                                    AppuiClass.cin.clear();
                                                    per_moral.stream().forEach(i-> AppuiClass.cin.add((int)i.get("identifiant")));
                                                    boolean testper=AppuiClass.cin.stream().anyMatch(i-> i==prm.getIdentifiantEntreprise());
                                                    
                                                    if(!testper)
                                                    {
                                                        BasicDBObject doc2 = new BasicDBObject("nomTitulaire",prm.getNomTitulaire())
                                                        .append("adressTitulaire", prm.getAdressTitulaire())
                                                        .append("nomCommercial", prm.getNomCommercial())
                                                        .append("codeBanque", espacebanque.getCodeBanque())
                                                        .append("identifiant", prm.getIdentifiantEntreprise());
                                                        coll_personne_moral.insert(doc2);
                                                    }
                                                    
                                                    BasicDBObject doc1 = new BasicDBObject("iban",cbe.getIban())
                                                    .append("codeBanque", espacebanque.getCodeBanque())
                                                    .append("dateDernierVersement", cbe.getDateDernierVersement())
                                                    .append("dateMAJ", cbe.getDateMAJ())
                                                    .append("regimeFiscale", cbe.getRegimeFiscale())
                                                    .append("decouvertAutoriser", cbe.getDecouvertAutoriser())
                                                    .append("etat", cbe.getEtat())
                                                    .append("solde", cbe.getSolde()) 
                                                    .append("type", cbe.getType())
                                                    .append("titulaire", prm.getIdentifiantEntreprise())
                                                    .append("dateCreation", cbe.getDateCreation());
                                                    coll_compte_entreprise.insert(doc1);
                                                    System.out.println("Operation effectuer avec succée");
                                                    break;
                                                    
                                                }
                                        
                                        case 3: {
                                                    String ibn;
                                                    boolean test,testexi,testdeb;
                                                    int tt;
                                                    double dbt;
                                                    do
                                                    {
                                                        System.out.println("entrer le iban de votre compte");
                                                        System.out.print(espacebanque.getCodeBanque());
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
                                                        AppuiClass.ibn=espacebanque.getCodeBanque()+ibn;
                                                        testexi=espacebanque.getComptes().stream().anyMatch(i-> i.getIban().compareTo(AppuiClass.ibn)==0);
                                                        if(ibn.length()!=17)
                                                        {
                                                            System.out.println("nombre de chiffres entré différent de 17");
                                                        }
                                                        else
                                                        {
                                                            if(!testexi)
                                                            {
                                                                System.out.println("compte inexistant");
                                                            }
                                                        }
                                                        
                                                    }while((!test)||(!testexi));
                                                    
                                                    System.out.println("entrer le montant à debiter");
                                                    dbt=sc.nextDouble();
                                                    testdeb=espacebanque.debiterCompte(AppuiClass.ibn, dbt,coll_compte_particulier,coll_compte_entreprise);
                                                    break;
                                                }
                                           
                                        case 4: {
                                                    String ibn;
                                                    boolean test,testexi,testdeb;
                                                    int tt;
                                                    double dbt;
                                                    do
                                                    {
                                                        System.out.println("entrer l'iban de votre compte");
                                                        System.out.print(espacebanque.getCodeBanque());
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
                                                        AppuiClass.ibn=espacebanque.getCodeBanque()+ibn;
                                                        testexi=espacebanque.getComptes().stream().anyMatch(i-> i.getIban().compareTo(AppuiClass.ibn)==0);
                                                        if(ibn.length()!=17)
                                                        {
                                                            System.out.println("nombre de chiffres entré différent de 17");
                                                        }
                                                        else
                                                        {
                                                            if(!testexi)
                                                            {
                                                                System.out.println("compte inexistant");
                                                            }
                                                        }
                                                        
                                                    }while((!test)||(!testexi));
                                                    
                                                    System.out.println("entrer le montant à créditer");
                                                    dbt=sc.nextDouble();
                                                    testdeb=espacebanque.crediterCompte(AppuiClass.ibn, dbt,coll_compte_particulier,coll_compte_entreprise);
                                                    break;
                                                }
                                        
                                        case 5: {
                                                    String ibn;
                                                    boolean test,testexi,testdeb;
                                                    int tt;
                                                    double dbt;
                                                    do
                                                    {
                                                        System.out.println("entrer l'iban de votre compte");
                                                        System.out.print(espacebanque.getCodeBanque());
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
                                                        AppuiClass.ibn=espacebanque.getCodeBanque()+ibn;
                                                        testexi=espacebanque.getComptes().stream().anyMatch(i-> i.getIban().compareTo(AppuiClass.ibn)==0);
                                                        if(ibn.length()!=17)
                                                        {
                                                            System.out.println("nombre de chiffres entré différent de 17");
                                                        }
                                                        else
                                                        {
                                                            if(!testexi)
                                                            {
                                                                System.out.println("compte inexistant");
                                                            }
                                                        }
                                                        
                                                    }while((!test)||(!testexi));
                                                    
                                                    String ibnsource = AppuiClass.ibn;
                                                    
                                                    do
                                                    {
                                                        System.out.println("entrer l'iban de compte destinataire");
                                                        
                                                        ibn = sc.next();
                                                        if(ibn.length()!=24)
                                                        {
                                                            System.out.println("L'iban doit contenir 24 caractères");
                                                        }
                                                    }while(ibn.length()!=24);
                                                    
                                                    String ibndest=ibn;
                                                    
                                                    System.out.println("entrer le montant à virer");
                                                    dbt=sc.nextDouble();
                                                    
                                                    testdeb=espacebanque.virerArgent(ibnsource,ibndest,dbt,coll_compte_particulier,coll_compte_entreprise);
                                                    break;
                                                    
                                                }
                                        
                                        case 6: {
                                                    String ibn;
                                                    boolean test,testexi,testdeb;
                                                    int tt;
                                                    double dbt;
                                                    do
                                                    {
                                                        System.out.println("entrer l'iban de votre compte");
                                                        System.out.print(espacebanque.getCodeBanque());
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
                                                        AppuiClass.ibn=espacebanque.getCodeBanque()+ibn;
                                                        testexi=espacebanque.getComptes().stream().anyMatch(i-> i.getIban().compareTo(AppuiClass.ibn)==0);
                                                        if(ibn.length()!=17)
                                                        {
                                                            System.out.println("nombre de chiffre entrée différent de 17");
                                                        }
                                                        else
                                                        {
                                                            if(!testexi)
                                                            {
                                                                System.out.println("compte inexistant");
                                                            }
                                                        }
                                                        
                                                    }while((!test)||(!testexi));
                                                    
                                                    testdeb=espacebanque.fermerCompte(AppuiClass.ibn,coll_compte_particulier,coll_compte_entreprise);
                                                    break;
                                                }
                                        
                                       
                                        case 7: {
                                                    float tx;
                                                    do
                                                    {
                                                        System.out.println("Entrer le taux d'interêt à appliqué aux differents comptes");
                                                        tx=sc.nextFloat();
                                                    }while((tx<0)||(tx>100));
                                                    
                                                    AppuiClass.soldes.clear();
                                                    espacebanque.getComptes().stream().forEach(i-> AppuiClass.soldes.add(i.getSolde()));
                                                    
                                                    if(AppuiClass.soldes.size()>0)
                                                    {
                                                        Object[] tauxtab=AppuiClass.soldes.toArray();
                                                        
                                                        ArrayList<Double> resSolde= calcultaux(tx,tauxtab);
                                                        for(int i=0;i<resSolde.size();i++)
                                                        {
                                                            espacebanque.getComptes().get(i).setSolde(resSolde.get(i));
                                                        }
                                                        System.out.println("Ajout de taux d'interet effectuer avec succée");
                                                        espacebanque.getComptes().stream().forEach(i-> i.afficher());
                                                    } 
                                                    else
                                                    {
                                                        System.out.println("erreur");
                                                    }
                                                    
                                                    break;
                                                }
                                        
                                        
                                        case 8: {
                                                    System.out.println("entrer le nombre d'année à partir du quel votre client sera recomoensé");
                                                    int nb= sc.nextInt();
                                                    System.out.println("entrer le nombre des points de fidélité de la recompense");
                                                    int fid = sc.nextInt();
                                                    
                                                    	
                                                    LocalDate finalDate=LocalDate.now();
                                                    
                                                    espacebanque.getComptes().stream().forEach(i-> {
                                                        AppuiClass.initialDate=LocalDate.parse(i.getDateCreation().substring(0, 10));
                                                        AppuiClass.deffDate=Period.between(AppuiClass.initialDate, finalDate).getYears();
                                                        
                                                        if(AppuiClass.deffDate>= nb)
                                                        {
                                                            if(i.getTitulaire() instanceof Personne_physique)
                                                            {
                                                                if(i.getEtat().compareTo("fermer")!=0)
                                                                {
                                                                    System.out.println(AppuiClass.deffDate);
                                                                    
                                                                    AppuiClass.pr= (Personne_physique)i.getTitulaire();
                                                              
                                                                    AppuiClass.pr.ajoutRecompense(fid, coll_personne_physique);
                                                                    System.out.println("operation effectuer avec succée");
                                                                }
                                                                
                                                              /*
                                                                AppuiClass.pr= (Personne_physique)i.getTitulaire();
                                                              
                                                                AppuiClass.pr.ajoutRecompense(fid, coll_personne_physique);
                                                             */
                                                            }
                                                        }
                                                    });
                                                    
                                                    
                                                }
                                        
                                    }
                                }while(choix_menu!=9);
                             }
                }
                
            }while(choix_banque!=3);
        }catch(Exception e)
        {
            System.out.println(e);
            remain();
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         remain();
    }
    
    
    
}
