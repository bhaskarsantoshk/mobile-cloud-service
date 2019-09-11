package org.magnum.dataup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.magnum.dataup.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VideoController {
	@Autowired
	private Map<Long, Video> videos = new HashMap<>();
	
	// GET /video 
	@RequestMapping (value="/video", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideos(){
		Collection<Video> videoList = new ArrayList<>();
		for ( long id: videos.keySet()) {
			videoList.add(videos.get(id));
		}
		return videoList;
	}
	
}
