package edu.sjsu.cmpe.library;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
	
	
	
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
	bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	
	log.debug("Queue name is {}. Topic name is {}", queueName,
		topicName);

	
	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	BookRepositoryInterface bookRepository = new BookRepository(configuration);
	environment.addResource(new BookResource(bookRepository));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
	
	ExecutorService executor = Executors.newFixedThreadPool(1);
	final BackgroundListenerThread listener=new BackgroundListenerThread(configuration, bookRepository);
	    Runnable backgroundTask = new Runnable() {

	   @Override
	   public void run() {
		   
		   try{
				listener.mainListener();
		   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
		   }
	   

	};
	executor.execute(backgroundTask);
    }
}
