package global;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Yaml;
import play.mvc.Action;
import play.mvc.Http.Request;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.TxRunnable;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        InitialData.insert();
    }
    
    @Override
    public Action onRequest(Request req, Method method) {
    	 InitialData.insert();
    	 return super.onRequest(req, method);
    }
    
    private static class InitialData {
        
    	@Transactional
        public static void insert() {
            if(Ebean.find(User.class).findRowCount() == 0) {
            	
            	Ebean.execute(new TxRunnable() {
					
					@Override
					public void run() {
						
						Logger.info("Chargement des données initiales");
		                
		                Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

		                Ebean.save(all.get("users"));
		                for(Object user: all.get("users")) {
		                    Ebean.saveManyToManyAssociations(user, "following");
		                }
		                
		                Ebean.save(all.get("messages"));
		                
		                Ebean.save(all.get("mentions"));
		                
		                Logger.info("Chargement des données initiales terminé avec succès");
		                
						
					}
				});
            	
            }
        }
        
    }
}
