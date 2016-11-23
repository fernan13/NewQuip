<?php 

    session_start();
    require_once('connectvars.php');
    require_once('functions.php');

    if ( isset( $_POST['submit']) ) {
        
        //Debemos de obtener todos los _IDS de los elementos que se encuentran en la papelera
        $conn   = mysqli_connect( SERVER, USER, PASS, BD );
                    
        $query  = "select elementos._ID from elementos where elementos.Papelera = 1";                
        $res    = mysqli_query( $conn, $query );
        $correo = $_SESSION['correo'];
        
        if ( $res ) {
            
            while ( $row = mysqli_fetch_array( $res ) ) {
                
                if ( isset( $_POST[$row[0]] ) ) {
                    
                    $valor  = $_POST[$row[0]];
                    
                    if ( $valor == 1 ) {
                        
                        //Recuperar
                        $query = "update elementos set Papelera = 0 where _ID = $row[0]";
                        mysqli_query($conn, $query);
                        generarActualizacionWeb( $conn, $row[0], $correo );
                        
                    }
                    else
                    {
                        //Eliminar del todo
                        eliminarElementoWeb( $conn, $row[0], $correo );
                    }
                }
            }
        }
    }

?>

<html>

    <head>
        <meta charset="utf-8"/>
        <title>Papelera</title>
    </head>
    
    <body>
        <h1>Papelera</h1>
        <?php
                
            $conn   = mysqli_connect( SERVER, USER, PASS, BD );
            $correo = $_SESSION['correo'];        
            $query  = "select elementos._ID, elementos.Titulo, elementos.Contenido, media.BlobType, elementos.Tipo from elementos left join media on elementos._ID = media.ParentId where elementos.Papelera = 1 AND elementos.Email = $correo";
                
            $res    = mysqli_query( $conn, $query );
                
            //Significa que existen notas que no estan en la papelera
            if ( mysqli_num_rows( $res ) > 0 ) {
             
                //Montamos el formulario
                        
                echo '<form method="post" action="'.$_SERVER['PHP_SELF'].'">';
                    
                    echo '<table border = "1">';
                
                        echo '<tr>';
                
                            echo '<th>Id</th>';
                            echo '<th>Titulo</th>'; 
                            echo '<th>Cuerpo</th>'; 
                            echo '<th>Imagen</th>';
                            echo '<th>Recuperar</th>';      
                            echo '<th>Eliminar</th>';
                
                        echo '<tr>';
                
                        //Obtenemos los datos
                        while ( $row = mysqli_fetch_array($res) ) {
                            
                            $id     = $row['_ID'];
                            $titulo = $row['Titulo'];
                            $cuerpo = $row['Contenido'];
                            $imagen = empty($row['BlobType']) == 1 ? "No" : "Si";
                            $tipo   = $row['Tipo'];
                            
                            echo '<tr>';
                            
                                echo "<td>$id</td>";
                                echo "<td>$titulo</td>";
                            
                                if ( $tipo == 1 ) {
                                    
                                    $elementos = json_decode($cuerpo);
                                    
                                    $items      = count($elementos);
                                    $done       = 0;
                                    $no_done    = 0;
                                    
                                    foreach( $elementos as $objetos ) {
                                        
                                        $valores    = get_object_vars($objetos);
                                        
                                        $valores['check'] == 1 ? $done++ : $no_done++;
                                    }
                                    
                                    $cuerpo = "Elementos en la lista: $items\nElementos realizados: $done\nElementos no realizados : $no_done\n";
                                    
                                }
                            
                                echo '<td><textarea rows="3" cols="50" readonly>'.$cuerpo.'</textarea></td>';
                                echo "<td>$imagen</td>";
                                echo '<td><input type="radio" name="'.$id.'" value="1"/></td>';
                                echo '<td><input type="radio" name="'.$id.'" value="2"/></td>';
                            
                            echo '</tr>';
                        }
                
                    echo '</table>';
                
                    
                    echo '<p><input type="submit" name="submit" value="Actualizar"/></p>';
                
                echo '</form>';
            
            }
            else
            {
                echo '<p>No existen elementos en la papelera</p>';
            }
        
            //Cerramos la conexion
            mysqli_close($conn);
        ?>
        <p><a href="notasuser.php">Volver atras</a></p>
    </body>
</html>