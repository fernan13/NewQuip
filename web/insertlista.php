<?php

    require_once('connectvars.php');
    require_once('functions.php');
    session_start();

?>

<html>

    <head>
        <meta charset="utf-8"/>
        <title>Agregar Lista</title>
        <script src="js/scripts.js"></script>
        
    </head>
    <body>
    
        <h1>Agregar Lista</h1>
        <form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
            <p>Titulo: <input type="text" name="titulo"/></p>
            
            <table id="dataTable" border="1">
                <thead>
                    
                    <tr>
                        <th>Eliminar</th>
                        <th>Realizado</th>
                        <th>No Realizado</th>
                        <th>Tarea</th>
                    </tr>
                    
                </thead>
                <tbody>
                    
                    <tr>
                        <td><input type="checkbox" name="del"/></td>
                        <td><input type="radio" name="0" value="1"/></td>
                        <td><input type="radio" name="0" value="2" checked/></td>
                        <td><input type="text"  name="texto[]"/> </td>

                    </tr>
                </tbody>
                
            </table>
            <!-- Botón para agregar filas -->
            <input type="button" name="ne" value="N.Elemento" onclick="addRow('dataTable');"/>
            <input type="button" name="ee" value="E.Elemento" onclick="deleteRow('dataTable');"/>

            <p><input type="submit" name="submit" value="Agregar"/></p>
            
        </form>
        
        <p> <a href="index.php">Volver atras</a></p>
    </body>
</html>

<?php 
    if ( isset( $_POST['submit'] ) ) {
        
        //En primer lugar comprobamos que exista informacion
        $flag = false;
        
        foreach( $_POST['texto'] as $info ) {
            
            if ( !empty($info) ) {
                
                $flag = true;
                break;
            }
        }
      
        //Si existe informacion la almacenamos
        if ( $flag ) {
            
            $info = array();
            
            /*
                1. Obtenemos los indices de cada check para buscar su valor en $_POST
                2. Buscamos toda la informacion y la almacenamos en un array siguiendo el
                   formato utilizado en android
                3. Generamos el elemento json y lo insertamos en la BBDD   
            */
            $keys = array_keys($_POST);
            $keys_check = array();
            
            foreach ( $keys as $key_value ) {
                
                if ( is_numeric($key_value) ) $keys_check[] = $key_value;
            }
            
            for( $i = 0; $i < count($keys_check); $i++ ) {
            
                $info_row = array(
                    
                    "check" => $_POST[$keys_check[$i]] == 1 ? true : false,
                    "texto" => $_POST['texto'][$i]
                );
                
                $info[] = $info_row;
            }
            //Guardamos la informacion en la BBDD
            $titulo         = $_POST['titulo'];
            $contenido      = json_encode($info);
            $correo         = $_SESSION['correo'];
            
            $conn           = mysqli_connect( SERVER, USER, PASS, BD );
            $query          = "insert into elementos ( Titulo, Contenido, Tipo, Papelera, Notificacion, Email, 
                            Color, Orden ) values ( '$titulo', '$contenido', 1, 0, '', $correo, 0, 0)";
            
            $res            = mysqli_query( $conn, $query );
            
            if ( $res ) {
                
                 
                //Obtenemos el id asignado
                $query_id   = "select _id from elementos order by _id desc limit 1";
                $res_id     = mysqli_query($conn, $query_id);
                $id_lista   = mysqli_fetch_array($res_id)[0];
                
                $query_up   = "update elementos set Orden = $id_lista where _ID = $id_lista";
                mysqli_query( $conn, $query_up );
                
                generarActualizacionWeb( $conn, $id_lista, $correo );
                
                header('Location: notasuser.php');
            }
            
        }
        else
        {
            echo '<p>Nota no insertada: No existe informacion</p>';
        }
    } 
?>
