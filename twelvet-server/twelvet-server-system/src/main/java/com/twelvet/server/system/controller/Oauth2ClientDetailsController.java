package com.twelvet.server.system.controller;

import com.twelvet.api.system.domain.SysClientDetails;
import com.twelvet.framework.core.application.controller.TWTController;
import com.twelvet.framework.core.application.domain.JsonResult;
import com.twelvet.framework.jdbc.web.page.TableDataInfo;
import com.twelvet.framework.jdbc.web.utils.PageUtils;
import com.twelvet.framework.log.annotation.Log;
import com.twelvet.framework.log.enums.BusinessType;
import com.twelvet.framework.security.utils.SecurityUtils;
import com.twelvet.framework.utils.$;
import com.twelvet.framework.utils.StringUtils;
import com.twelvet.server.system.service.ISysClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author twelvet
 * @WebSite www.twelvet.cn
 * @Description: 终端配置 信息操作处理
 */
@Api(value = "Oauth2ClientDetailsController", tags = { "终端配置" })
@RestController
@RequestMapping("/client")
public class Oauth2ClientDetailsController extends TWTController {

	@Autowired
	private ISysClientDetailsService sysClientDetailsService;

	/**
	 * 查询终端配置列表
	 * @param sysClientDetails SysClientDetails
	 * @return JsonResult<TableDataInfo>
	 */
	@ApiOperation(value = "查询终端配置列表")
	@PreAuthorize("@role.hasPermi('system:client:list')")
	@GetMapping("/pageQuery")
	public JsonResult<TableDataInfo> pageQuery(SysClientDetails sysClientDetails) {
		PageUtils.startPage();
		List<SysClientDetails> list = sysClientDetailsService.selectSysClientDetailsList(sysClientDetails);
		return JsonResult.success(PageUtils.getDataTable(list));
	}

	/**
	 * 获取终端配置详细信息
	 * @param clientId 终端ID
	 * @return JsonResult<SysClientDetails>
	 */
	@ApiOperation(value = "获取终端配置详细信息")
	@PreAuthorize("@role.hasPermi('system:client:query')")
	@GetMapping(value = "/{clientId}")
	public JsonResult<SysClientDetails> getInfo(@PathVariable("clientId") String clientId) {
		return JsonResult.success(sysClientDetailsService.selectSysClientDetailsById(clientId));
	}

	/**
	 * 新增终端配置
	 * @param sysClientDetails SysClientDetails
	 * @return JsonResult<String>
	 */
	@ApiOperation(value = "新增终端配置")
	@PreAuthorize("@role.hasPermi('system:client:insert')")
	@Log(service = "终端配置", businessType = BusinessType.INSERT)
	@PostMapping
	public JsonResult<String> insert(@RequestBody SysClientDetails sysClientDetails) {
		String clientId = sysClientDetails.getClientId();
		if (StringUtils.isNotNull(sysClientDetailsService.selectSysClientDetailsById(clientId))) {
			return JsonResult.error("新增终端'" + clientId + "'失败，编号已存在");
		}
		sysClientDetails.setClientSecret(SecurityUtils.encryptPassword(sysClientDetails.getClientSecret()));
		return json(sysClientDetailsService.insertSysClientDetails(sysClientDetails));
	}

	/**
	 * 修改终端配置
	 * @param sysClientDetails sysClientDetails
	 * @return JsonResult<String>
	 */
	@ApiOperation(value = "修改终端配置")
	@PreAuthorize("@role.hasPermi('system:client:update')")
	@Log(service = "终端配置", businessType = BusinessType.UPDATE)
	@PutMapping
	public JsonResult<String> update(@RequestBody SysClientDetails sysClientDetails) {
		// 重新设置密码
		if ($.isNotEmpty(sysClientDetails.getClientSecret())) {
			sysClientDetails.setClientSecret(SecurityUtils.encryptPassword(sysClientDetails.getClientSecret()));
		}

		return json(sysClientDetailsService.updateSysClientDetails(sysClientDetails));
	}

	/**
	 * 删除终端配置
	 * @param clientIds 终端ID数组
	 * @return 成功删除个数
	 */
	@ApiOperation(value = "删除终端配置")
	@PreAuthorize("@role.hasPermi('system:client:remove')")
	@Log(service = "终端配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{clientIds}")
	public JsonResult<String> remove(@PathVariable String[] clientIds) {
		return json(sysClientDetailsService.deleteSysClientDetailsByIds(clientIds));
	}

}
