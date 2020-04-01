package sk.upjs.nosql_data_source.persist;

import java.util.List;

import sk.upjs.nosql_data_source.entity.Download;

public interface DownloadDao {

	List<Download> getAllDownloads();

	Download getDownloadById(long downloadId);

}