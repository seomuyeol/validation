package hello.itemservice.web.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

//	@ResponseBody  RestController가 있으면 자동으로 붙음 
	// RequestBody 사용해서  itemSaveForm  필드들을 api 로 받는다.
	@PostMapping("/add")
	public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {
		log.info("API controller 호출");
		
		if (bindingResult.hasErrors()) {
			log.info("검증 오류 발생 errors={}", bindingResult);
			return bindingResult.getAllErrors();
		}
		
		log.info("성공 로직 실행");
		return form;
	}
}
