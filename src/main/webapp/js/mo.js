

//主程序   aco
function insertAndSelectGa() {
    var population_size = $('#population_size').textbox('getValue');
    var generations = $('#generations').textbox('getValue');
    var pchange = $('#pchange').textbox('getValue');
    var pc = $('#pc').textbox('getValue');

    $('#query_list_ga').datagrid({
        method:"get",
        url: "http://127.0.0.1:8086/ga/insertAndSelectGa",
        queryParams:{
            population_size:population_size,
            generations:generations,
            pchange:pchange,
            pc:pc
        }
    });
}
