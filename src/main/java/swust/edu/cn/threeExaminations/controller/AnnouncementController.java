package swust.edu.cn.threeExaminations.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import swust.edu.cn.threeExaminations.model.Announcement;
import swust.edu.cn.threeExaminations.model.Area;
import swust.edu.cn.threeExaminations.model.Attachment;
import swust.edu.cn.threeExaminations.model.User;
import swust.edu.cn.threeExaminations.service.AnnouncementService;
import swust.edu.cn.threeExaminations.service.AreaService;
import swust.edu.cn.threeExaminations.service.AttachmentService;
import swust.edu.cn.threeExaminations.service.UserService;

@Controller
@RequestMapping("/announcementController")
public class AnnouncementController {
	List<Announcement> publicAnnoList = new ArrayList<Announcement>();

	public void setPublicAnnoList(List<Announcement> publicAnnoList) {
		this.publicAnnoList = publicAnnoList;
	}

	private AnnouncementService annoService;

	private UserService userService;

	private AreaService areaService;

	private AttachmentService attaService;

	public AttachmentService getAttaService() {
		return attaService;
	}

	@Autowired
	public void setAttaService(AttachmentService attaService) {
		this.attaService = attaService;
	}

	public AreaService getAreaService() {
		return areaService;
	}

	@Autowired
	public void setAreaService(AreaService areaService) {
		this.areaService = areaService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public AnnouncementService getAnnoService() {
		return annoService;
	}

	@Autowired
	public void setAnnoService(AnnouncementService annoService) {
		this.annoService = annoService;
	}

	// 发布公告，将公告相关信息存入数据库
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/publishAnnouncement")
	public ModelAndView publishAnnouncement(String title, String content,
			String attaSystemName, String attaPageName,HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			HttpSession session = request.getSession();
			User user = new User();
			user = (User) session.getAttribute("user");
			if (user == null) {
				map.put("result", Boolean.FALSE);
				map.put("message", "用户已经退出！");
			} else {
				Announcement announcement = new Announcement();
				announcement = annoService.selectAnnoByTitle(title);
				if (announcement != null) {
					map.put("result", Boolean.FALSE);
					map.put("message", "已存在该主题的公告，请重新命名！");
				} else {
					annoService.publishAnnouncement(title, content,
							user.getUserId());
					announcement = annoService.selectAnnoByTitle(title);
					int annoId = announcement.getAnnoId();
					// String realPath =
					// request.getSession().getServletContext().getRealPath("/");
					// System.out.println(realPath + "+++++++++++");
					if (attaPageName.length() > 0) {//解析页面和系统显示的附件名
						// 添加附件
						attaPageName.substring(0, attaPageName.length() - 2);// 去掉最后一个，
						attaSystemName.substring(0,attaSystemName.length()-2);
						String[] termIdStringP = attaPageName.split(",");// 间接收到的字符串按，进行分割,组成数组
						String[] termIdStringS = attaSystemName.split(",");
						for (int i = 0; i < termIdStringP.length; i++) {
							attaService.addAttachment(annoId,  termIdStringS[i],
									termIdStringP[i],"/upload/"+ termIdStringS[i]);//公告id，路径，系统名，页面名
							// System.out.println(termIdString[i]);
						}
						
					}
					map.put("result", Boolean.TRUE);
					map.put("message", "success");
				}
			}
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	// 修改公告，更新数据库中的公告信息
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/editAnnouncement")
	public ModelAndView editAnnouncement(Integer annoId, String title,
			String content, String attaSystemName, String attaPageName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			Announcement announcement = new Announcement();
			announcement = annoService.selectAnnoByTitle(title);
			if (announcement != null && !announcement.getAnnoId().equals(annoId)) {
				map.put("result", Boolean.FALSE);
				map.put("message", "已存在该主题的公告，请重新命名！");
			} else {
			annoService.editAnnouncement(annoId, title, content);
			// 删除该公告原来的所有附件
			attaService.deleteAttaByAnnoId(annoId);
			// 重新添加所有的公告附件
			if (attaPageName.length() > 0) {
				attaPageName.substring(0, attaPageName.length() - 2);// 去掉最后一个,
				attaSystemName.substring(0, attaSystemName.length() - 2);
				String[] termIdStringP = attaPageName.split(",");// 间接收到的字符串按,进行分割,组成数组
				String[] termIdStringS = attaSystemName.split(",");
				for (int i = 0; i < termIdStringP.length; i++) {
					attaService.addAttachment(annoId,termIdStringS[i], 
							termIdStringP[i],"/upload/" + termIdStringS[i]);
					// System.out.println(termIdString[i]);
				}
			}
			map.put("result", Boolean.TRUE);
			map.put("message", "success");
			}
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	// 删除某公告中的某一附件
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/deleteAttachment")
	public ModelAndView deleteAttachment(Integer attaId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			Announcement anno = new Announcement();
			Attachment atta = new Attachment();
			atta = attaService.findAttaByAttaId(attaId);
			anno = annoService.selectAnnoByAnnoId(atta.getAttaAnnoid());
			attaService.deleteAttaByAttaId(attaId);
			map.put("result", Boolean.TRUE);
			map.put("message", "success");
			map.put("annoucemnet", anno);
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	// 删除某一公告，即修改数据库中的公告状态，让其不显示
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/deleteAnnouncement")
	public ModelAndView deleteAnnouncement(Integer annoId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			annoService.deleteAnnouncement(annoId);
			map.put("result", Boolean.TRUE);
			map.put("message", "success");
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	// 显示当前用户发布的所有公告的列表
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/showAnnoList")
	public ModelAndView showAnnoList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			HttpSession session = request.getSession();
			User user = new User();
			user = (User) session.getAttribute("user");
			if (user == null) {
				map.put("result", Boolean.FALSE);
				map.put("message", "用户已经退出！");
			} else {
				List<Announcement> annoList = new ArrayList<Announcement>();
				// System.out.println(user.getUserName());
				annoList = annoService.showAnnoList(user.getUserId());
				if (annoList.size() > 0) {
					setPublicAnnoList(annoList);//publicAnnoList为所有记录列表
					int recordCount = annoList.size();//总记录数
					int pageCount;//总页数
					int temp = recordCount % 10;//10条记录一页
					if(temp == 0){
						pageCount = recordCount/10;
					}else{
						pageCount = recordCount/10 + 1;
					}
					List<Announcement> recordFirst = new ArrayList<Announcement>();//第一页记录
					int max = 0;
					int page = 1;//第一页
					max = publicAnnoList.size()<(10*page)?publicAnnoList.size():(10*page);//第page页的最大记录数
					for(int j = (page-1)*10;j<max;j++){//循环获取第page页的记录
						recordFirst.add(publicAnnoList.get(j));
					}
					//System.out.println(recordFirst.get(0).getAnnoId()+"公告dddddddd");
					map.put("annoRecordFirst", recordFirst);//第一页的记录
					map.put("annoList", annoList);//所有记录
					map.put("pageCount", pageCount);//总页数
					map.put("result", Boolean.TRUE);
					map.put("message", "success");
				} else {
					map.put("result", Boolean.FALSE);
					map.put("message", "还未发布公告，公告列表为空！");
				}
			}
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	//翻页显示记录，自己发布的公告
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/findRecordByPage")
	public ModelAndView findRecordByPage(int page,HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			HttpSession session = request.getSession();
			User user = new User();
			user = (User) session.getAttribute("user");
			if (user == null) {
				map.put("result", Boolean.FALSE);
				map.put("message", "用户已经退出！");
			} else {				
					List<Announcement> recordCurrent = new ArrayList<Announcement>();//第一页记录
					int max = 0;
					max = publicAnnoList.size()<(10*page)?publicAnnoList.size():(10*page);//第page页的最大记录数
					for(int j = (page-1)*10;j<max;j++){//循环获取第page页的记录
						recordCurrent.add(publicAnnoList.get(j));
					}
					//System.out.println(recordFirst.get(0).getAnnoId()+"公告dddddddd");
					map.put("annoRecordCurrent", recordCurrent);//第一页的记录
					map.put("result", Boolean.TRUE);
					map.put("message", "success");
			}
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}
	
	// 显示当前用户选中的某一条公告的详细内容
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/showAnnoContent")
	public ModelAndView showAnnoContent(Integer annoId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			Announcement announcement = new Announcement();
			// 获取该公告信息
			announcement = annoService.showAnnoContent(annoId);
			// System.out.println(announcement.getAnnoTitle()+":"+announcement.getAnnoContent());

			// 获取公告发布时间,最后修改时间为准
			Date lastModiftTime = announcement.getAnnoLastmodifytime();// 获取时间戳
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(lastModiftTime);

			// 获取公告发布人的单位
			User annoUser = new User();
			int annoUserId = announcement.getAnnoUserid();
			// System.out.println("公告发布人:"+annoUserId);
			annoUser = userService.findUserById(annoUserId);
			int areaId = annoUser.getUserAreaid();
			Area area = new Area();
			area = areaService.getAreaByAreaId(areaId);
			String areaName = area.getAreaName();
			while (area.getAreaParentid() != 0) {
				areaId = area.getAreaParentid();
				area = areaService.getAreaByAreaId(areaId);
				areaName = area.getAreaName() + areaName;
			}

			// 公告对应的附件
			List<Attachment> attaList = new ArrayList<Attachment>();
			attaList = attaService.findAttaByAnnoId(annoId);
			map.put("result", Boolean.TRUE);
			map.put("message", "success");
			map.put("announcement", announcement);
			map.put("date", date);
			map.put("publishUnit", areaName);
			map.put("publishUser", annoUser.getUserName());
			map.put("attaList", attaList);
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	// 显示当前用户可查看的所有公告的列表
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping("/lookAnnoList")
	public ModelAndView lookAnnoList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			HttpSession session = request.getSession();
			User user = new User();
			user = (User) session.getAttribute("user");// 当前用户
			if (user == null) {
				map.put("result", Boolean.FALSE);
				map.put("message", "用户已经退出！");
			} else {
				List<Announcement> annoList = new ArrayList<Announcement>();// 用户可以查看的公告列表
				List<Announcement> annoList1 = new ArrayList<Announcement>();
				annoList1 = annoService.lookAnnoList();// 数据库中所有不被删除的公告列表
				//System.out.println(annoList1.get(0).getAnnoTitle()+"ggggggggggggggggggggggggg");
				if (annoList1.size() > 0) {
					int areaId;
					Area area = new Area();
					List<Area> areaList = new ArrayList<Area>();//当前用户可查看的公告的所有地区列表
					areaId=user.getUserAreaid();//当前用户地区
					area = areaService.getAreaByAreaId(areaId);
					areaList.add(area);
					while(area.getAreaParentid() != 0){
						areaId = area.getAreaParentid();
						area = areaService.getAreaByAreaId(areaId);
						areaList.add(area);
					}
					
					int i,j;
					User annoUser = new User();// 公告发布者
					Area annoArea = new Area();
					//int annoNum=0;//显示的所有公告条数
					for (i = 0; i < annoList1.size(); i++) {
						/*if(annoNum >= 20){
							break;
						}else{*/
						annoUser = userService.findUserById(annoList1.get(i)
								.getAnnoUserid());// 公告发布者
						annoArea = areaService.getAreaByAreaId(annoUser
								.getUserAreaid());// 公告发布者的地区
						for(j=0;j<areaList.size();j++){
							if(annoArea.getAreaId().equals(areaList.get(j).getAreaId())){
								annoList.add(annoList1.get(i));
								//annoNum++;
								break;
							}
						//}
						}
					}
					if (annoList.size() == 0) {
						map.put("result", Boolean.FALSE);
						map.put("message", "还未发布公告，公告列表为空！");
					} else {
						//分页
						setPublicAnnoList(annoList);
						int recordCount = annoList.size();//总记录数
						int pageCount;//总页数
						int temp = recordCount % 10;//10条记录一页
						if(temp == 0){
							pageCount = recordCount/10;
						}else{
							pageCount = recordCount/10 + 1;
						}
						List<Announcement> recordFirst = new ArrayList<Announcement>();//第一页记录
						int max = 0;
						int page = 1;//第一页
						max = publicAnnoList.size()<(10*page)?publicAnnoList.size():(10*page);//第page页的最大记录数
						for(int k = (page-1)*10;k<max;k++){//循环获取第page页的记录
							recordFirst.add(publicAnnoList.get(k));
						}
						// 最新公告的信息，默认显示的公告
						Announcement newAnno = annoList
								.get(0);
						Date lastModiftTime = newAnno.getAnnoLastmodifytime();// 获取时间戳
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String date = sdf.format(lastModiftTime);
						// 获取最新公告的公告发布人的单位
						User annoUserNew = new User();
						int annoUserId = newAnno.getAnnoUserid();
						annoUserNew = userService.findUserById(annoUserId);
						int areaIdNew = annoUserNew.getUserAreaid();
						Area areaNew = new Area();
						areaNew = areaService.getAreaByAreaId(areaIdNew);
						String areaName = areaNew.getAreaName();
						while (areaNew.getAreaParentid() != 0) {
							areaIdNew = areaNew.getAreaParentid();
							areaNew = areaService.getAreaByAreaId(areaIdNew);
							areaName = areaNew.getAreaName() + areaName;
						}
						
						// 获取最新公告的附件
						List<Attachment> attaList = new ArrayList<Attachment>();
						attaList = attaService.findAttaByAnnoId(newAnno
								.getAnnoId());
						map.put("annoList", recordFirst);
						map.put("pageCount", pageCount);//总页数
						map.put("result", Boolean.TRUE);
						map.put("message", "success");
						map.put("newAnno", newAnno);
						map.put("date", date);
						map.put("newUnit", areaName);
						map.put("publishUser", annoUserNew.getUserName());
						map.put("attaList", attaList);
					}
				} else {
					map.put("result", Boolean.FALSE);
					map.put("message", "还未发布公告，公告列表为空！");
				}
			}
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
	}

	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping(value = "/uplaodAttachment", method = RequestMethod.POST)
	public ModelAndView uplaodAttachment(MultipartFile postfile,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			// 业务逻辑
			String oriFileName = postfile.getOriginalFilename();
			System.out.println(oriFileName + "++++++++++");
			String extension=oriFileName.substring(oriFileName.lastIndexOf(".")).toLowerCase();//源文件的后缀名
			String newFileName = System.currentTimeMillis()+extension;//系统文件名以时间戳命名
			String path = request.getSession().getServletContext()
					.getRealPath("upload/" + newFileName);//服务器上文件保存的路径
			System.out.println(path + "++++++++++");
			postfile.transferTo(new File(request.getSession()
					.getServletContext().getRealPath("upload/" + newFileName)));

			map.put("pageName", oriFileName);//页面显示的文件名
			map.put("systemName", newFileName);//系统中保存的文件名
			map.put("path", path);//文件在服务器上的路径
			map.put("result", Boolean.TRUE);
			map.put("message", "success");
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！!!!!");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			mav.toString();
			return mav;
		}
	}

	// 通过附件在数据库中的id下载附件
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping(value = "/downloadAttachment", method = RequestMethod.GET)
	public ModelAndView downloadAttachment(Integer id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			System.out.println(id + "zzzzzzzzzzzzz");
			// 业务逻辑
			// 封装下载
			// path是指欲下载的文件的路径。
			Attachment attachment = new Attachment();
			attachment = attaService.findAttaByAttaId(id);

			String path = request.getSession().getServletContext()
					.getRealPath(attachment.getAttaUploadpath());
			System.out.println(path + "fffffffffffff");
			File file = new File(path);
			// 取得文件名。
			//String filename = file.getName();//获取服务器上保存的文件名
			String filename = attachment.getAttaPagename();//获取数据库中保存的页面的文件名
			System.out.println(filename+"文件名");
			// 取得文件的后缀名。
			//String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header，即下载的下来的文件名
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes("GBK"),"ISO8859_1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！!!!!");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			mav.toString();
			return mav;
		}
	}

	// 通过附件名下载附件，附件路径没有存入数据库时
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping(value = "/downloadAttachment2", method =RequestMethod.GET)
	public ModelAndView downloadAttachment2(String downloadPath,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map map = new HashMap();
		try {
			System.out.println(downloadPath +"zzzzzzzzzzzzz");
			// 业务逻辑
			// 封装下载
			// path是指欲下载的文件的路径。
			String[] m=downloadPath.split(",");//解析uploadPath中的附件的系统和页面名
			String systemName = m[0];//取得服务器是的文件名
			String filename = m[1];// 取得下载另存的文件名。
			String path = request.getSession().getServletContext()
					.getRealPath("/upload/"+systemName);
			System.out.println(path + "fffffffffffff");
			File file = new File(path);
			// 取得文件名。
			//String filename = file.getName();
			// 取得文件的后缀名。
			//String ext = filename.substring(filename.lastIndexOf(".") + 1)					.toUpperCase();

			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes("GBK"),"ISO8859_1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
			map.put("result", Boolean.FALSE);
			map.put("message", "执行出现出错！!!!!");
			e.printStackTrace();
		} finally {
			view.setAttributesMap(map);
			mav.setView(view);
			mav.toString();
			return mav;
		}
	}

}
