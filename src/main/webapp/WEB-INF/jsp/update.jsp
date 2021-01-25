<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/7/27
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>UPDATE</title>
</head>
<body>
    <%--<table border="1px" cellspacing="1"></table>--%>
    <form action="update">
        <lable>学号</lable><input type="text" name="sid" value="${st.sid}"><br>
        <lable>姓名</lable><input type="text" name="sname" value="${st.sname}"><br>
        <lable>年龄</lable><input type="text" name="sage" value="${st.sage}"><br>

        <lable>性别</lable><input type="radio" name="sgender" value="male" ${"male" eq st.sgender?"checked":""}>male
                           <input type="radio" name="sgender" value="female" ${"female" eq st.sgender?"checked":""}>female<br>

        <lable>地址</lable><input type="text" name="saddress" value="${st.saddress}"><br>

        <lable>班级名</lable>
                <select name="classId">
                    <c:forEach items="${stuClasses}" var="c">
                        <option value="${c.classId}" <c:if test="${c.classId==st.classId}" >selected</c:if> >${c.className}</option>
                    </c:forEach>
                </select><br>

       <input type="submit" value="提交">

    </form>
    
    


</body>
</html>
