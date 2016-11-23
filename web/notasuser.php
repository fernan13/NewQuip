<?php
    
    require_once('connectvars.php');
    session_start();
?>

<html>

    <head>
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <?php
            
            $correo = $_SESSION['correo'];
            $conn   = mysqli_connect( SERVER, USER, PASS, BD );
            
            $query  = "select elementos._id, elementos.Titulo, elementos.Contenido, elementos.Tipo, 
                       media.BlobType from elementos left join media on elementos._id = media.parentid 
                       where elementos.Papelera = 0 AND elementos.Email = '$correo'";

            $res    = mysqli_query( $conn, $query );
            
            if ( $res ) {
                    
                while ( $row = mysqli_fetch_array( $res ) ) {
                        
                    echo '<p><fieldset style="width:25%">'; 
                        
                        echo "<p><strong>Titulo: </strong>".$row['Titulo']."</p>";
                    
                        if ( $row['Tipo'] == 0 ) {

                            echo "<p><strong>Contenido: </strong>".$row['Contenido']."</p>";
                            
                            $imagen = $row['BlobType'];
                            
                            if ( !empty($imagen) ) {
                                
                                echo '<p>Imagen: <img src="data:image/png;base64,'.base64_encode($imagen).'" width="100" height="100"/></p>';
                            }
                            
                            echo '<p><a href="updatenota.php?nota_id='.$row['_id'].'">Editar nota id '.$row['_id'].'</a></p>';
                        }
                        else
                        {
                            $elementos = json_decode($row['Contenido']);
                                    
                             $items      = count($elementos);
                             $done       = 0;
                             $no_done    = 0;
                                    
                             foreach( $elementos as $objetos ) {
                                        
                                $valores    = get_object_vars($objetos);
                                        
                                $valores['check'] == 1 ? $done++ : $no_done++;
                            }
                                    
                            echo "<p><strong>Elementos en la lista: </strong>$items</p>";
                            echo "<p><strong>Elementos realizados: </strong>$done</p>";
                            echo "<p><strong>Elementos no realizados : </strong>$no_done</p>";
                            
                            echo '<p><a href="updatelista.php?lista_id='.$row['_id'].'">Editar lista id '.$row['_id'].'</a></p>';
                        }

                    echo '</fieldset></p>';
                }
            }
        ?>
        
        <p><a href="insertnota.php">Agregar Nota</a></p>
        <p><a href="insertlista.php">Agregar Lista</a></p>
        <p><a href="delete.php">Eliminar Elementos</a></p>
        <p><a href="papelera.php">Papelera</a></p>
        <p><a href="cerrar_sesion.php">Cerrar Sesion</a></p>
    </body>
</html>
