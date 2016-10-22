package edu.csupomona.cs585.ibox;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import edu.csupomona.cs585.ibox.sync.FileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

public class IntegrationTest {
	
	public Drive drive;
	public String orginalPath;
	public FileSyncManager FSM;
	public Drive googleDriveClient;
	public WatchDir watchdir;
	GoogleDriveFileSyncManager GoogleDriveFSM;
	
	boolean Passes = false;
	public boolean GetFilesFromDrive(String Filename) throws IOException
	{
		List call = googleDriveClient.files().list();
		FileList files = call.execute();
		for(com.google.api.services.drive.model.File file : files.getItems())
		{
			if(Filename.equals(file.getTitle()));
			{
				Passes = true;
			}
		}
		return Passes;
	}
	public String GetFileName(File localFile) throws IOException
	{
		String Name= "";
		
		List Call = googleDriveClient.files().list();
		FileList files = Call.execute();
		for(com.google.api.services.drive.model.File file : files.getItems())
		{
			
			if(file.getTitle().equals(localFile.getName()));
			{
				Name = file.getTitle();
			}
		}
		return Name;
	}
	// prepares to make authorized API calls by using the service account's credentials to request an access token from the OAuth 2.0 auth server.
	
	@Before
	public void PreparingForTest() throws IOException{
		 HttpTransport httpTransport = new NetHttpTransport();
	       JsonFactory jsonFactory = new JacksonFactory();

	       try{
	           GoogleCredential credential = new  GoogleCredential.Builder()
	             .setTransport(httpTransport)
	             .setJsonFactory(jsonFactory)
	             .setServiceAccountId("797284670004-vk9mqm3qvsq13mqv2958cjvurcsfjrt3.apps.googleusercontent.com")
	             .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
	             .setServiceAccountPrivateKeyFromP12File(new File("src/ibox-10f25f138b8c.p12"))
	             .build();

	           googleDriveClient = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("ibox").build();  
	       }catch(GeneralSecurityException e){
	           e.printStackTrace();
	       }
		GoogleDriveFSM = new GoogleDriveFileSyncManager(drive);
		orginalPath = System.getProperty("user.dir");
		File Test = new File("NewFile");
		if(!Test.exists())
		{
			Test.mkdir();
		}

	}
	@Test
	public void AddFileTest() throws IOException, InterruptedException {

		File InsertlocalFile = new File(orginalPath+"\\src\\Test.txt");
		if(!InsertlocalFile.exists())
		{
			InsertlocalFile.createNewFile();
		}
		Thread.sleep(1000); 
	}
	@Test
	public void GetFileIDTest() throws IOException, InterruptedException {
		Thread thread3 = new Thread(){
			public void run() {
				try
				{
					File local_fileID = new File(orginalPath+"\\src\\Test.txt");
					String FileID = "";
					if(!local_fileID.exists())
					{
						local_fileID.createNewFile();
					}
					Thread.sleep(1000);
					FileID = GoogleDriveFSM.getFileId(local_fileID.getName());
					Assert.assertNotNull(FileID);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		thread3.start();
		
		
	}
	
	@Test(expected = Exception.class)
	public void DeleteFileTest() throws IOException, InterruptedException {

		Thread thread = new Thread () {
			  public void run () {

				  try
				  {
					  File localFileDelete = new File(orginalPath+"\\src\\Test.txt");
						if(!localFileDelete.exists())
						{
							localFileDelete.createNewFile();
						}
						localFileDelete.delete();
						Thread.sleep(5000);
				  }
				  catch(Exception e)
				  {
					  e.printStackTrace();
				  }
				  
			  }
			};
			thread.start();
		Assert.assertNull(GoogleDriveFSM.getFileId("\\src\\Test.txt"));
	}
	

	@Test
	public void UpdateFileTest() throws IOException, InterruptedException {
		
		Thread thread2 = new Thread () {
			
		  public void run() {
		    try
		    {
		    	File localFileUpdate = new File(orginalPath+"\\src\\Test.txt");
		    	if(!localFileUpdate.exists())
		    	{
		    		localFileUpdate.createNewFile();
		    	}
		    	Thread.sleep(3000);
				FileWriter FWriter = new FileWriter(localFileUpdate);
				FWriter.write("Update File Test - Integration Test");
				FWriter.close();
				String GetFileName =localFileUpdate.getName();
				System.out.println(GetFileName);
				String fileid = GoogleDriveFSM.getFileId(localFileUpdate.getName());
				Assert.assertNotNull(fileid);
		    }
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		  }
		};thread2.start();
	}
	
}