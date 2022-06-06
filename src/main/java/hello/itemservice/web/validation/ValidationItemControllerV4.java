package hello.itemservice.web.validation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }
   
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    	
    	//model.addAttribute("itemSaveForm", form); 모델 애트리뷰트 item 기입 생략시 이렇게 들어가서 문제가 생김
    	
    	log.info("objectName={}", bindingResult.getObjectName()); //오브젝트 내용을 가지고 있다. 
    	log.info("target={}", bindingResult.getTarget());
    	
    	//특정 필드가 아닌 복합 룰 검증
    	if (form.getPrice() != null && form.getQuantity() != null) {
    		int resultPrice = form.getPrice() * form.getQuantity();
    		if (resultPrice < 10000) {
    			bindingResult.reject("totalPriceMin", new Object[] {1000, resultPrice}, null);
;    		}
    	}
    	
    	//검증에 실패하면 다시 입력 폼으로
    	if (bindingResult.hasErrors()) {
    		log.info("errors = {} ", bindingResult);
    		return "validation/v4/addForm";
    	}
    	
    	//성공로직
    	
    	Item item = new Item();
    	item.setItemName(form.getItemName());
    	item.setPrice(form.getPrice());
    	item.setQuantity(form.getQuantity());
    	
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }
    
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }
    
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
        
        //특정 필드가 아닌 복합 룰 검증
    	if (form.getPrice() != null && form.getQuantity() != null) {
    		int resultPrice = form.getPrice() * form.getQuantity();
    		if (resultPrice < 10000) {
    			bindingResult.reject("totalPriceMin", new Object[] {1000, resultPrice}, null);
;    		}
    	}
        
    	//검증에 실패하면 다시 입력 폼으로
    	if (bindingResult.hasErrors()) {
    		log.info("errors = {} ", bindingResult);
    		return "validation/v4/editForm";
    	}
    	
    	Item itemParam = new Item();
    	itemParam.setItemName(form.getItemName());
    	itemParam.setPrice(form.getPrice());
    	itemParam.setQuantity(form.getQuantity());
    	
    	itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }

}