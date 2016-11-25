<?php
    
    session_start();
    require_once('connectvars.php');
    require_once('functions.php');

    //Conexion BBDD
    $conn   = mysqli_connect( SERVER, USER, PASS, BD );
    
    //Variables utilizadas para obtener el contenido de la BBDD y mostrarlo en el formulario
    $titulo;
    $cuerpo;
        
    //Con el simbolo & delante le pasamos la referencia de nuestra variable
    function load_data( $id, &$titulo, &$cuerpo ) {
        
        $query  = "select elementos._id, elementos.titulo, elementos.contenido, elementos.notificacion from elementos where elementos._id = $id";
        
        $conn   = mysqli_connect( SERVER, USER, PASS, BD );
        $res    = mysqli_query( $conn, $query );
        
        //Variables donde almacenaremos el contenido
        
        if ( $res ) {
            
            $row    = mysqli_fetch_array($res);
            
            $titulo = $row['titulo'];
            $cuerpo = $row['contenido'];
        }
        
        mysqli_close($conn);
    }

    //Si es la primera vez que entra
    if ( isset($_GET['lista_id']) ) {
        
        $id = $_GET['lista_id'];
        load_data( $id, $titulo, $cuerpo );
        
    }

    //Si venimos del formulario
    if ( isset($_POST['submit']) ) {
        
        //Id de la nota a actualizar
        $id             = $_POST['lista_id'];
        
        //Correo de actualizacion
        $correo         = $_SESSION['correo'];
        
        //Conexion con la BBDD
        $conn           = mysqli_connect( SERVER, USER, PASS, BD );
        
        //En primer lugar comprobamos que exista informacion
        $flag = false;
        
        foreach( $_POST['texto'] as $info ) {
            
            if ( !empty($info) ) {
                
                $flag = true;
                break;
            }
        }
      
        //Si existe informacion la actualizamos
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
            $titulo_post    = $_POST['titulo'];
            $contenido_post = json_encode($info);            
            
            $query          = "update elementos set Titulo = '$titulo_post', Contenido = '$contenido_post' where _ID = $id";
            
            $res            = mysqli_query( $conn, $query );
            
            if ( $res ) {
                
                //Debemos de generar la actualizacion
                generarActualizacionWeb( $conn, $id, $correo );
                echo '<p>Lista actualizada correctamente</p>';
                
                //Cargamos la informacion en el formulario
                load_data( $id, $titulo, $cuerpo);
            }
            
        }
        else
        {
            //Eliminamos la lista directamente
            $query_del  = "delete from elementos where _ID = $id";
            $res        = mysqli_query($conn, $query );
            
            if ( $res ) {
                
                echo '<script type="text/javascript">alert("Lista eliminada");</script>';
                header('Location: notasuser.php');
            }
        }
     
        mysqli_close($conn);
    }
?>

<html>

    <head>
        <meta charset="utf-8"/>
        <title>Actualizar Lista</title>
        <script src="js/scripts.js"></script>
        
    </head>
    <body>
    
        <h1>Actualizar Lista</h1>
        <form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
            <p>Titulo: <input type="text" name="titulo" value = "<?php echo $titulo;?>" /></p>
            
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
                    
                    <?php
                    
                        $elementos  = json_decode($cuerpo);
                        $cont       = 0;
                    
                        foreach ( $elementos as $objeto ) {
                            
                            $valores    = get_object_vars($objeto);
                            
                            $realizado  = $valores['check'];
                            $tarea      = $valores['texto'];
                            
                            echo '<tr>';
                            
                                echo '<td><input type="checkbox" name="del"/></td>';
                            
                                if ( $realizado == 1 ) {
                                    
                                    echo '<td><input type="radio" name="'.$cont.'" value="1" checked/></td>';
                                    echo '<td><input type="radio" name="'.$cont.'" value="2"/></td>';
                                }
                                else
                                {
                                    echo '<td><input type="radio" name="'.$cont.'" value="1"/></td>';
                                    echo '<td><input type="radio" name="'.$cont.'" value="2" checked/></td>';
                                }
                            
                                echo '<td><input type="text" name="texto[]" value="'.$tarea.'"></td>';
                            
                            echo '</tr>';
                            
                            $cont++;
                        }
                    ?>
                </tbody>
                
            </table>
            <!-- Botón para agregar filas -->
            <input type="button" name="ne" value="N.Elemento" onclick="addRow('dataTable');"/>
            <input type="button" name="ee" value="E.Elemento" onclick="deleteRow('dataTable');"/>
            
            <!-- Input Hidden para reenviar por POST el id de la lista -->
            <input type="hidden" name="lista_id" value="<?php if ( isset( $_GET['lista_id']) ) echo $_GET['lista_id']; else echo $_POST['lista_id']; ?>"/>
            <p><input type="submit" name="submit" value="Actualizar"/></p>
            
        </form>
        
        <p> <a href="notasuser.php">Volver atras</a></p>
        
    </body>

</html>