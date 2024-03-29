package br.com.miqueiascoutinho.aws.apis.controllers;

import java.io.File;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.Bucket;

import br.com.miqueiascoutinho.aws.apis.controllers.errors.ApiError;
import br.com.miqueiascoutinho.aws.apis.services.s3.AmazonS3ReadOperations;
import br.com.miqueiascoutinho.aws.apis.services.s3.AmazonS3WriteOperations;
import br.com.miqueiascoutinho.aws.apis.tos.AwsBucket;
import br.com.miqueiascoutinho.aws.apis.tos.AwsBucketFile;
import br.com.miqueiascoutinho.aws.apis.tos.AwsS3File;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(path = "/v1/aws/s3")
@Api(tags = "API Amazon S3", value = "Controller com exemplo de integração com Amazon S3")
public class AmazonS3Controller {

	@Autowired
	private AmazonS3ReadOperations amazonS3ReadOperations;
	
	@Autowired
	private AmazonS3WriteOperations amazonS3WriteOperations;
	
	@GetMapping(path = "/list-buckets")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Operação responsável em listar os buckets no Amazon S3")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AwsBucket.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class), })
	public List<AwsBucket> listAwsS3Buckets() {
		return amazonS3ReadOperations.listBuckets();
	}

	@GetMapping(path = "/bucket-content")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation(value = "Operação responsável em listar o conteúdo de um determinado bucket no Amazon S3")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AwsBucketFile.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class), })
	public List<AwsBucketFile> listBucketContent( @RequestParam(value = "bucket-name") String bucketName) {
			
		return amazonS3ReadOperations.listBucketContent(bucketName);
	}
	
	@PostMapping(path = "/bucket")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(value = "Operação responsável em criar um bucket no Amazon S3."
			+ "Regras para criar um bucket, sendo o mesmo padrão de DNS: "
			+ "1) O nome do bucket deve ter entre 3 e 63 caracteres; "
			+ "2) O nome do bucket não pode conter espaços; "
			+ "3) O nome do bucket deve ser minúsculo (lowerCase); "
			+ "4) O nome do bucket é único/universal, não podendo existir 2 buckets com o mesmo nome (mesmo que em regiões diferentes).")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Sucesso"),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class), })
	public Bucket createBucket(@RequestBody AwsBucket bucket) {
		return amazonS3WriteOperations.createBucket(bucket);
	}
	
	@DeleteMapping(path = "/bucket/{bucket-name}")
	@ApiOperation(value = "Operação responsável em deletar um bucket no Amazon S3")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class), 
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class)})
	public void deleteBucket(@PathVariable(value = "bucket-name") String bucketName) {
		amazonS3WriteOperations.deleteBucket(bucketName);
	}
	
	@PutMapping(path = "/bucket/{bucket-name}")
	@ApiOperation(value = "Operação responsável em realizar o put (upload) de um arquivo local para um bucket específico no Amazon S3")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class), 
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class)})
	public void putObject (@Valid @RequestBody AwsS3File file,
			@PathVariable(value = "bucket-name") String bucketName)  {
		
		File f = new File(file.getFile());
		amazonS3WriteOperations.putObject(bucketName,
				file.getFileName(), 
				f);
	}
	
}
