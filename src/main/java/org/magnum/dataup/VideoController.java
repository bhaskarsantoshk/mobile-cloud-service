package org.magnum.dataup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController {
	@Autowired
	private VideoFileManager fileManager;
	private Map<Long, Video> videos = new HashMap<>();
	private static final AtomicLong currentId = new AtomicLong(0L);
	
	// GET /video 
	@RequestMapping (value="/video", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideos(){
		Collection<Video> videoList = new ArrayList<>();
		for ( long id: videos.keySet()) {
			videoList.add(videos.get(id));
		}
		return videoList;
	}
	
	// GET /video/{id}/data
	@RequestMapping ( value="/video/{id}/data", method= RequestMethod.GET)
	public void getVideo(@PathVariable("id") long id , HttpServletResponse response) throws IOException {
		Video video = videos.get(id);
		if ( video != null ) {
			fileManager.copyVideoData(video, response.getOutputStream());
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	// POST /video
	@RequestMapping (value ="/video", method = RequestMethod.POST)
	public @ResponseBody Video addMetaData(@RequestBody Video video) {
		return save(video);
	}

	private Video save(Video video) {
		// TODO Auto-generated method stub
		if ( video.getId() == 0) {
			video.setId(currentId.incrementAndGet());
		}
		video.setDataUrl(getDataUrl(video.getId()));
		videos.put(video.getId(), video);
		return video;
	}

	private String getDataUrl(long videoId) {
		// TODO Auto-generated method stub
		HttpServletRequest request =  
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String urlLocalServer = "http://"+request.getServerName()+((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
		return urlLocalServer + "/video/" + videoId + "/data";
	}
	
	//POST /video/{id}/data
	
	 @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
	    public @ResponseBody VideoStatus addSomeVideo(@PathVariable("id") long id,
	                                                  @RequestParam("data") MultipartFile videoData,
	                                                  HttpServletResponse response) throws IOException {
	        if (!videos.containsKey(id)) {
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            return null;
	        }

	        fileManager.saveVideoData(videos.get(id), videoData.getInputStream());

	        return new VideoStatus(VideoStatus.VideoState.READY);
	    }
	
}
