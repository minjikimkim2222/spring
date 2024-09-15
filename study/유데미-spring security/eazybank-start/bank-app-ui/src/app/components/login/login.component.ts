import { Component, OnInit } from '@angular/core';
import { User } from "src/app/model/user.model";
import { NgForm } from '@angular/forms';
import { LoginService } from 'src/app/services/login/login.service';
import { Router } from '@angular/router';
import { getCookie } from 'typescript-cookie';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  authStatus: string = "";
  model = new User();

  constructor(private loginService: LoginService, private router: Router) {

   }

  ngOnInit(): void {

  }

  validateUser(loginForm: NgForm) {
    this.loginService.validateLoginDetails(this.model).subscribe(
      responseData => {
        this.model = <any> responseData.body;
        
        // 백엔드로부터 해당 CSRF 토큰값 읽어오고, 후속 요청에 세션에서 쿠키값 저장해서 쓸 수 있게 추가

        let xsrf = getCookie('XSRF-TOKEN')!;
        window.sessionStorage.setItem("XSRF-TOKEN", xsrf);

        // 백엔드로부터 Authorization 헤더값을 읽어와서 + sessionStorage에 적용
        window.sessionStorage.setItem("Authorization", 
              responseData.headers.get('Authorization')!);

        this.model.authStatus = 'AUTH';
        window.sessionStorage.setItem("userdetails",JSON.stringify(this.model));
        this.router.navigate(['dashboard']);
      });

  }

}
