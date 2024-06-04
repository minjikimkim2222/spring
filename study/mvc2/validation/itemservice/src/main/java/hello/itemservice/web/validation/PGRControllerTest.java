package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Post;
import hello.itemservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PGRControllerTest {
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/postForm")
    public String postForm(Model model){
        model.addAttribute("post2", new Post());
        return "prg/postForm";
    }

//    @PostMapping("/submitPost")
//    public String submitPost(@ModelAttribute Post post, Model model){
//        postRepository.save(post);
//
//        model.addAttribute("message", "Post submitted successfully!");
//        return "prg/postConfirmation";
//    }

    @PostMapping("/submitPost")
    public String submitPost(@ModelAttribute Post post, Model model){
        postRepository.save(post);

        //model.addAttribute("message", "Post submitted successfully!");
        return "redirect:/posts";
    }
    @GetMapping("/posts")
    @ResponseBody
    public List<Post> getPosts(){
        return postRepository.getPosts();
    }

}
