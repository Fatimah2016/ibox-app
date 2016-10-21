package edu.csupomona.cs585.ibox;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

import com.google.api.services.drive.*;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Files.Update;



public class UnitTest {
	public GoogleDriveFileSyncManager GoogleDriveFileSM;
	public Drive mockDrive;
	public Files mockFiles;
	public Insert mockInsert;
	public List mockList;
	public Delete mockDelete;
	public Update mockUpdate;
	public java.io.File file;
	public FileList filelist;
	ArrayList<File> arraylist;
	
	@Before
	public void PreperingForTest() throws IOException
	{
		mockFiles = mock(Files.class);
		mockDrive = mock(Drive.class);
		mockInsert = mock(Insert.class);
		mockDelete = mock(Delete.class);
		mockUpdate = mock(Update.class);
		mockList = mock(List.class);
		
		
		file = mock(java.io.File.class);
		filelist = new FileList();
		arraylist = new ArrayList<File>();
		GoogleDriveFileSM = new GoogleDriveFileSyncManager(mockDrive);
		// all the following are stubbing 
		// inserting file test 
		when(mockDrive.files()).thenReturn(mockFiles);
		when(mockFiles.insert(isA(File.class),isA(AbstractInputStreamContent.class))).thenReturn(mockInsert);
		when(mockInsert.execute()).thenReturn(new File());
		
		// geting File ID test
		when(file.getName()).thenReturn("FileName");
		when(mockFiles.list()).thenReturn(mockList);
		when(mockList.execute()).thenReturn(filelist);
		arraylist.add(new File().setTitle(file.getName()).setId("0"));
		
		//deleting file test
		when(mockFiles.delete(isA(String.class))).thenReturn(mockDelete);
		when(mockDelete.execute()).thenReturn(null);
		
		//updating file test 
		when(mockFiles.update(isA(String.class), isA(File.class),isA(FileContent.class))).thenReturn(mockUpdate);
		when(mockUpdate.execute()).thenReturn(new File());
		
	}
	@Test
	public void addFileTest() throws IOException{
		//Method Call
		GoogleDriveFileSM.addFile(file);
		
		verify(mockDrive).files();
		verify(mockFiles).insert(isA(File.class),isA(AbstractInputStreamContent.class));
		verify(mockInsert).execute();
		

	}
	@Test
	public void GetFileIDTest() throws IOException {
		//Method Call
		filelist.setItems(arraylist);
		
		GoogleDriveFileSM.getFileId(file.getName());

		verify(mockDrive).files();
		verify(mockFiles).list();
		verify(mockList).execute();

	}
	
	@Test
	public void deleteFileTest() throws IOException{
		
		filelist.setItems(arraylist);
		//Method Call
		GoogleDriveFileSM.deleteFile(file);

		verify(mockFiles).delete(isA(String.class));
		verify(mockDelete).execute();
		
	}
	
	
	@Test
	public void UpdateFileTest() throws IOException {
		filelist.setItems(arraylist);
		//Method Call
		GoogleDriveFileSM.updateFile(file);
		
		verify(mockFiles).update(isA(String.class),isA(File.class), isA(FileContent.class));
		verify(mockUpdate).execute();
	}
	

}