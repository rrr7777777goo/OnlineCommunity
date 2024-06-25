# 🧾 온라인 커뮤니티 사이트 구현

여러가지 주제에 대해서 사람들과 이야기를 나눌 수 있는 온라인 커뮤니티 사이트를 구현해보는 프로젝트입니다. 

## 프로젝트 기능 및 설계

- 로그인 관련 기능
  - 회원가입을 해서 계정을 생성할 수 있다.
  - 회원가입 후에는 아이디와 비밀번호 검증을 해서 로그인을 할 수 있다. 이 때 로그인은 JWT 인증 방식으로 이루어진다.
  - 각 계정에는 DB 아이디, 로그인용 아이디, 비밀번호, 닉네임, 역할, 생성날짜 정보가 존재한다.
  - 아이디는 중복을 허용하지 않으며 생성 후 수정도 불가능하다.
  - 비밀번호, 닉네임은 수정이 가능하다. 이 때 로그인을 하듯이 한번 더 아이디,비밀번호 검증을 진행해야 한다.
  - 역할은 사이트 관리자, 일반 사용자를 구분하기 위한 용도로 사용이 된다.
  - 자신의 계정을 삭제할 수 있다. 삭제를 하기 전에 한번 더 아이디, 비밀번호 검증이 필요하며 계정 삭제를 할 경우 자신이 지금까지 작성했던 모든 게시글, 댓글, 대댓글도 같이 제거된다.
    
- 주제 관련 기능
  - 게시글을 분류할 수 있는 주제를 생성하거나 수정, 삭제할 수 있다.
  - 각 주제는 DB 아이디, 주제이름, 생성날짜 정보가 존재한다.
   
- 게시글 관련 기능
  - 로그인 여부에 상관없이 게시글을 조회할 수 있다. 이 때 게시글을 한 번 조회할 때 마다 조회 횟수 정보가 1씩 증가한다.
  - 로그인한 사용자는 게시글을 생성하거나 수정, 삭제할 수 있으며 삭제를 할 경우에는 그 게시글에 있던 댓글과 대댓글들도 모두 삭제가 된다.
  - 각 게시글에는 DB 아이디, 주제 아이디, 유저 아이디, 제목, 내용, 생성날짜, 수정날짜, 조회 횟수 정보가 존재한다.
  - 각 게시글에는 해당 게시글의 좋아요, 싫어요 정보가 존재한다. 이 정보는 해당 정보를 저장하는 DB에 의해 따로 관리되며 한 계정당 한번씩만 좋아요와 싫어요 중 하나를 고를 수 있다. 또한, 나중에 선택한 것을 취소하거나 변경할 수도 있다.
  - 일반 사용자는 부적절한 게시글을 발견하면 신고할 수 있다. 신고를 하게 되면 해당 정보를 관리하는 DB에 저장이 되며 관리자가 확인 후 해당 게시글을 제거할 수 있다.

- 댓글 관련 기능
  - 로그인 여부에 상관없이 게시글에 달린 댓글을 조회할 수 있다.
  - 로그인한 사용자는 댓글을 생성하거나 수정, 삭제할 수 있으며 삭제를 할 경우에는 그 게시글에 있던 대댓글들도 모두 삭제가 된다.
  - 각 댓글에는 DB 아이디, 게시글 아이디, 유저 아이디, 내용, 생성날짜, 수정날짜 정보가 존재한다.
  - 각 댓글에는 해당 댓글의 좋아요, 싫어요 정보가 존재한다. 이 정보는 해당 정보를 저장하는 DB에 의해 따로 관리되며 한 계정당 한번씩만 좋아요와 싫어요 중 하나를 고를 수 있다. 또한, 나중에 선택한 것을 취소하거나 변경할 수도 있다.
  - 일반 사용자는 부적절한 댓글을 발견하면 신고할 수 있다. 신고를 하게 되면 해당 정보를 관리하는 DB에 저장이 되며 관리자가 확인 후 해당 게시글을 제거할 수 있다.
    
- 대댓글 관련 기능
  - 로그인 여부에 상관없이 댓글에 달린 대댓글을 조회할 수 있다.
  - 로그인한 사용자는 대댓글을 생성하거나 수정, 삭제할 수 있다.
  - 각 대댓글에는 DB 아이디, 댓글 아이디, 유저 아이디, 내용, 생성날짜, 수정날짜 정보가 존재한다.
  - 각 대댓글에는 해당 대댓글의 좋아요, 싫어요 정보가 존재한다. 이 정보는 해당 정보를 저장하는 DB에 의해 따로 관리되며 한 계정당 한번씩만 좋아요와 싫어요 중 하나를 고를 수 있다. 또한, 나중에 선택한 것을 취소하거나 변경할 수도 있다.
  - 일반 사용자는 부적절한 대댓글을 발견하면 신고할 수 있다. 신고를 하게 되면 해당 정보를 관리하는 DB에 저장이 되며 관리자가 확인 후 해당 게시글을 제거할 수 있다.

## ERD 
![ERD](src/main/resources/erd/online_community.png)

## Trouble Shooting
...

### Tech Stack
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white"> 
  <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white">
</div>
