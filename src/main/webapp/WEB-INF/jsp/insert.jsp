<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: songb
  Date: 2019/7/29
  Time: 22:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>INSERT</title>
</head>
<body>
<form action="insert" method="post">
    <%--<lable>学号</lable><input type="text" name="sid" ><br>--%>
    <lable>姓名</lable><input type="text" name="sname" ><br>
    <lable>年龄</lable><input type="text" name="sage" ><br>

    <lable>性别</lable><input type="radio" name="sgender" value="male">male
                        <input type="radio" name="sgender" value="female">female<br>

    <lable>地址</lable><input type="text" name="saddress" ><br>

    <lable>班级名</lable>
    <select name="classId">
        <option value="1">java130</option>
        <option value="2">ui</option>
        <option value="3">web</option>
    </select><br>

    <input type="submit" value="submit">

</form>



</body>
</html>
