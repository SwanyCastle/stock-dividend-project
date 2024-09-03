# 💵 주식 배당금 프로젝트
  > 주식의 티커명으로 회사정보, 배당금 정보를 스크래핑 해 배당금 지급내역을 응답해주는 프로젝트 
<br>
<br>

# 🛠️ 사용 기술
<table>
  <thead>
    <th>Name</th>
    <th>Version</th>
  </thead>
  <tbody>
    <tr>
      <td>Spring Boot</td>
      <td>3.3.3</td>
    </tr>
    <tr>
      <td>Gradle</td>
      <td>8.8</td>
    </tr>
    <tr>
      <td>Java</td>
      <td>17</td>
    </tr>
    <tr>
      <td>Redis</td>
      <td>7.4.0</td>
    </tr> 
    <tr>
      <td>H2</td>
      <td>2.2.224</td>
    </tr>    
    <tr>
      <td>Spring Data JPA</td>
      <td>3.3.3</td>
    </tr>  
    <tr>
      <td>jsoup</td>
      <td>1.17.2</td>
    </tr>
    <tr>
      <td>jjwt</td>
      <td>0.9.1</td>
    </tr>
  </tbody>
</table>

<br>
<br>

# 🗓️ 기능
#### 사용자
  - 사용자 생성 : Http Request 요청으로 사용자이름과 비밀번호를 받아 사용자이름 <br>
              사용자이름 중복체크하고 비밀번호 암호화해 DB에 저장
  - 사용자 로그인 : Http Request 요청으로 사용자이름과 비밀번호를 받아 해당 사용자가 DB 에 <br>
                존재하는지 확인 후 비밀번호가 같은지 체크한 후에 jwt 토큰을 발급해 응답
    
#### 회사
  - 회사 정보 및 배당금 정보 저장 : 티커명으로 회사 정보 및 배당금 정보를 스크래핑 후 DB 에 저장
  - 회사 정보 전체 조회 : 티커명으로 지금까지 검색한 회사들의 정보를 조회
  - 회사 이름 검색 자동완성 : keyword 입력으로 들어오는 값으로 시작하는 회사의 이름을 조회
  - 회사 정보 삭제 : 티커명에 해당하는 회사 정보 삭제

#### 배당금
  - 배당금 지급 내역 정보 조회 : 티커 명에 해단하는 배당급 지급 조회정보 조회
  
<br>
<br>

# API 스펙
<table>
  <thead>
    <th>분류</th>
    <th>기능</th>
    <th>URI</th>
    <th>Method</th>
    <th>Status Code</th>
  </thead>
  <tbody>
    <tr>
      <td rowspan="2">사용자</td>
      <td>사용자 생성</td>
      <td>/auth/sign-up</td>
      <td>POST</td>
      <td>200</td>
    </tr>
    <tr>
      <td>사용자 로그인</td>
      <td>/auth/sign-in</td>
      <td>POST</td>
      <td>200</td>
    </tr>
    <tr>
      <td rowspan="4">회사</td>
      <td>회사 및 배당금 정보 저장</td>
      <td>/company</td>
      <td>GET</td>
      <td>200</td>
    </tr>
    <tr>
      <td>회사 전체 조회</td>
      <td>/company</td>
      <td>PUT</td>
      <td>200</td>
    </tr>
    <tr>
      <td>회사 이름 자동 완성</td>
      <td>/company/auto-complete</td>
      <td>DELETE</td>
      <td>200</td>
    </tr>
    <tr>
      <td>회사 삭제</td>
      <td>/company/{ticker}</td>
      <td>DELETE</td>
      <td>200</td>
    </tr>
    <tr>
      <td>배당금</td>
      <td>배당금 지급 내역 조회</td>
      <td>/finance/dividend/{companyName}</td>
      <td>DELETE</td>
      <td>200</td>
    </tr>
  </tbody>
</table>

<br>
<br>
