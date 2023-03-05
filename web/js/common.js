//将传入的角色值转换为角色名称字符串
function convert2RoleName(role) {
    let name = "";
    switch (role) {
        case 0:
            name = "系统管理员";
            break;
        case 1:
            name = "售票员";
            break;
        case 2:
            name = "站务员";
            break;
        case 3:
            name = "司机";
            break;
        case 4:
            name = "行包员";
            break;
    }
    return name;
}
////将传入的long值转换为日期对象
function parse2Date(value) {
    let result=null;
    if(value){
        result=new Date(value);

    }return result;
}
///将传入的日期对象转换为yyy-mm-dd格式的字符串
function formatDate(d){
 let result=null;
if(d){
    let y = d.getFullYear();
    let m = d.getMonth() + 1;
    let d1 = d.getDate();
    result=y+"-"+((m<10)?("0"+m):m)+"-"+((d1<10)?("0"+d1):d1)
}
return result;
}