<?php
    
    session_start();
    require_once('connectvars.php');
    require_once('functions.php');
    
    //Conexion BBDD
    $conn   = mysqli_connect( SERVER, USER, PASS, BD );
    
    //Variables utilizadas para obtener el contenido de la BBDD y mostrarlo en el formulario
    $titulo;
    $cuerpo;
    $imagen;
        
    //Con el simbolo & delante le pasamos la referencia de nuestra variable
    function load_data( $id, &$titulo, &$cuerpo, &$imagen ) {
        
        $query  = "select elementos._id, elementos.titulo, elementos.contenido, elementos.notificacion, media.blobtype from elementos left join media on elementos._id = media.parentid where elementos._id = $id";
        
        $conn   = mysqli_connect( SERVER, USER, PASS, BD );
        $res    = mysqli_query( $conn, $query );
        
        //Variables donde almacenaremos el contenido
        
        if ( $res ) {
            
            $row    = mysqli_fetch_array($res);
            
            $titulo = $row['titulo'];
            $cuerpo = $row['contenido'];
            $imagen = $row['blobtype'];
        }
        
        mysqli_close($conn);
    }

    if ( isset($_GET['nota_id']) ) {
        
        $id = $_GET['nota_id'];
        load_data( $id, $titulo, $cuerpo, $imagen );
        
    }

    //Que realizamos si se ha pulsado el boton
    if ( isset($_POST['submit']) ) {
        
        $id             = $_POST['nota_id'];
        $titulo_post    = $_POST['titulo'];
        
        //Correo de actualizacion
        $correo         = $_SESSION['correo'];
        
        //Funcion para obtener la informacion almacenada en el textarea
        $cuerpo_post    = htmlspecialchars($_POST['cuerpo']);
        $imagen_post    = $_FILES['img']['tmp_name'];
        
        //Comprobamos los valores
        if ( !empty( $titulo_post ) && !empty( $cuerpo_post ) ) {
            
            $query_el   = "update elementos set Titulo = '$titulo_post', Contenido = '$cuerpo_post' where _id = $id";
            
            $res        = mysqli_query($conn, $query_el);
            
            if ( $res ) {
                
                //Actualizamos tanto si existe como si no existe imagen
                    
                $type       = $_FILES['img']['type'];
                        
                //FIle nos devuelve en un string el fichero que se le pase como parametro
                if ( !empty( $imagen_post ) ) {
                    
                    $imagen_post    = mysqli_real_escape_string( $conn, file_get_contents($imagen_post));
                }

                 //Puede pasar 2 cosas la primera que la nota no tuviese imagen y la segunda que si tuviese
                 
                $query_img      = "select BlobType from media where ParentId = $id";
                $res_img        = mysqli_query( $conn, $query_img );
                $query_media;
                
                if ( mysqli_num_rows( $res_img ) > 0 ) {
                    
                    $query_media    = "update media set MimeType = '$type', BlobType = '$imagen_post' where ParentId = $id";
                }
                else
                {
                    $query_media    = "insert into media ( MimeType, BlobType, ParentId ) values ( '$type', '$imagen_post', $id)";
                }
                        
                $res_media  = mysqli_query($conn, $query_media);

                if ( !empty($imagen) && $res_media ) {

                    //Eliminamos la imagen del servidor
                    @unlink($_FILES['img']['tmp_name']);
                }
                
                //Debemos de generar la actualizacion
                generarActualizacionWeb( $conn, $id, $correo );
                
                load_data( $id, $titulo, $cuerpo, $imagen );
                echo "<p>Nota actualizada correctamente</p>";
                mysqli_close($conn);
            }
            
        }
        
        else
        {
            if ( empty($titulo) ) {
                
                echo "Titulo vacio";
            }
            elseif ( empty($cuerpo) ) {
                
                echo "Cuerpo vacio";
            }
        }
    }


?>

<html>

    <head>
        <meta charset="utf-8"/>
        <title>Actualizar Nota</title>
    </head>

    <body>
        <h1>Actualizar Nota</h1>
        <form method="post" action="<?php echo $_SERVER['PHP_SELF']?>" id="form" 
              enctype="multipart/form-data">
        
            <table border ="0">
            
                <tr>
                    <td>Titulo: </td>
                    <td><input type="text" name="titulo" value="<?php if ( isset($_POST['titulo']) ) echo $_POST['titulo']; else echo $titulo;?>"/></td>
                </tr>
                <tr>
                
                    <td>Cuerpo: </td>
                    <td><textarea rows="5" cols="50" name="cuerpo" form="form"><?php if ( isset($_POST['cuerpo']) ) echo htmlspecialchars($_POST['cuerpo']); else echo $cuerpo;
                        ?></textarea></td>
                </tr>
                <?php
                    
                    echo '<tr><td>Imagen: </td>';
                
                    if ( empty($imagen) ) {
                        
                        echo '<td><input type="file" name="img" accept=".gif,.jpg,.jpeg,.png"/></td></tr>';
                    }
                    else
                    {
                        echo '<td><img src="data:image/png;base64,'.base64_encode($imagen).'" width="100" height="100"/></td></tr>';
                            
                        echo '  <tr>
                                    <td></td>
                                    <td><input type="file" name="img" accept=".gif,.jpg,.jpeg,.png"/></td>
                                </tr>';
                        }
                        
                ?>
                <tr>
                    <input type="hidden" name="nota_id" value="<?php if ( isset($_GET['nota_id']) ) echo $_GET['nota_id']; else echo $_POST['nota_id'];?>"/>
                    <td><input type="submit" name="submit" value="Actualizar"/></td>
                </tr>
            </table>
            
        </form>
        <p><a href="notasuser.php">Volver atras</a></p>
    </body>
</html>