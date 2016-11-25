function addRow(tableID) {

    var table = document.getElementById(tableID);

    var rowCount        = table.rows.length;
    var row             = table.insertRow(rowCount);

    var cell1           = row.insertCell(0);
    var element1        = document.createElement("input");

    element1.type       = "checkbox";
    cell1.appendChild(element1);
                
    var cell2           = row.insertCell(1);
    var element2        = document.createElement("input");

    element2.type       = "radio";
    element2.setAttribute("name", rowCount);
    element2.value      = "1";
    cell2.appendChild(element2);

    var cell3           = row.insertCell(2);
    var element3        = document.createElement("input");

    element3.type       = "radio";
    element3.setAttribute("name", rowCount);
    element3.value      = "2";
    element3.checked    = "true";
    cell3.appendChild(element3);
    
    var cell4           = row.insertCell(3);
    var element4        = document.createElement("input");

    element4.type       = "text";
    element4.setAttribute("name","texto[]");
    cell4.appendChild(element4);
}

 

function deleteRow(tableID) {

    try {

        var table = document.getElementById(tableID);
        var rowCount = table.rows.length;

        for(var i=0; i<rowCount; i++) {

            var row = table.rows[i];

            var chkbox = row.cells[0].childNodes[0];
                        
            if( null != chkbox && true == chkbox.checked) {

                table.deleteRow(i);
                rowCount--;
                
                i--;

            }
        }
        
        //Cambiamos los name para que no haya ningun problema en la insercion de nuevas filas
        for ( var i = 0; i < table.rows.length; i++ ) {
            
            var row = table.rows[i];
            
            var radio1 = row.cells[1].childNodes[0];
            var radio2 = row.cells[2].childNodes[0];
            
            radio1.name = i;
            radio2.name = i;
            
            radio1.value = "1";
            radio2.value = "2";
        } 

    }catch(e) { alert(e); }

}
