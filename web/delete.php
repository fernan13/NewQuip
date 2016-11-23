<?php 

    session_start();
    require_once('connectvars.php');
    require_once('functions.php');

    if ( isset( $_POST['submit']) ) {
        
        //Debemos de comprobar los ids de las notas que hay que enviar a la papelera
        if ( isset( $_POST['notas'] ) ) {
            
            $conn   = mysqli_connect( SERVER, USER, PASS, BD );
            $correo = $_SESSION['correo'];
            
            foreach ( $_POST['notas'] as $id ) {
                
                //Actualizamos sus registros en la tabla
                $query = "update elementos set Papelera = 1  where _ID = $id";
                mysqli_query($conn, $query);
                
                //Guardamos la actualizacion
                generarActualizacionWeb( $conn, $id, $correo );
            }
        }
    }

?>

<html>

    <head>
        <meta charset="utf-8"/>
        <title>Eliminar</title>
    </head>
    
    <body>
        <h1>Eliminar Elementos</h1>
        <?php
                
            $conn   = mysqli_connect( SERVER, USER, PASS, BD );
                 
            $correo = $_SESSION['correo'];        
            $query  = "select elementos._ID, elementos.Titulo, elementos.Contenido, media.BlobType, elementos.Tipo from elementos left join media on elementos._ID = media.ParentId where elementos.Papelera = 0 AND elementos.Email = $correo";
                
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
                                echo '<td><input type="checkbox" name="notas[]" value="'.$id.'"/></td>';
                            
                            echo '</tr>';
                        }
                
                    echo '</table>';
                
                    
                    echo '<p><input type="submit" name="submit" value="Eliminar"/></p>';
                
                echo '</form>';
            
            }
            else
            {
                echo '<p>No existen notas para eliminar</p>';
            }
        
            //Cerramos la conexion
            mysqli_close($conn);
        ?>
        <p><a href="notasuser.php">Volver atras</a></p>
    </body>
</html>