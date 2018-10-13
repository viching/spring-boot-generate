package com.viching.generate.context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import com.viching.generate.config.Engine;
import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.config.Investigator;
import com.viching.generate.config.JavaFormatter;
import com.viching.generate.config.App;
import com.viching.generate.config.MybatisConfig;
import com.viching.generate.converter.Converter;
import com.viching.generate.core.impl.GeneratedJavaFile;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.source.DBTableJavaBean;

/**
 * 
 * @author Administrator
 *
 */
public class Context {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Engine engine;
	@Autowired
	private Investigator investigator;
	@Autowired
	private ICommentGenerator iCommentGenerator;
	@Autowired
	private List<Converter> converters;
	@Autowired
	private JavaFormatter javaFormatter;
	@Autowired
	private MybatisConfig mybatisConfig;
	@Autowired
	private App app;
	
	private Set<String> projects = new HashSet<String>();
	
	public void openTheDoor(){
		List<DBTableJavaBean> dbTables = null;
		List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
		try {
			File project = new File(engine.getRoot(), engine.getProjectName());
			if(!project.exists()) {
				project.mkdirs();
			}
			generatePom(project);
			
			File mainFile = new File(project, "src/main");
			File mainJavaFile = new File(mainFile, "java");
			if(!mainJavaFile.exists()) {
				mainJavaFile.mkdirs();
			}
			File mainSourceFile = new File(mainFile, "resources");
			if(!mainSourceFile.exists()) {
				mainSourceFile.mkdirs();
			}
			File testFile = new File(project, "src/test");
			File testJavaFile = new File(testFile, "java");
			if(!testJavaFile.exists()) {
				testJavaFile.mkdirs();
			}
			File testSourceFile = new File(testFile, "resources");
			if(!testSourceFile.exists()) {
				testSourceFile.mkdirs();
			}
			//侦查解析数据库信息
			dbTables = investigator.introspectTables();
			List<CompilationUnit> units = null;
			GeneratedJavaFile gjf = null;
			if (dbTables != null) {
			  
	          for (DBTableJavaBean dbTable : dbTables) {
            	for(Converter converter : converters){
	            	units = converter.getCompilationUnits(dbTable);
	            	if(units == null || units.size() == 0){
	            		continue;
	            	}
	            	for (CompilationUnit compilationUnit : units) {
	                    gjf = new GeneratedJavaFile(compilationUnit, mainJavaFile.getAbsolutePath(), engine.getCharCode(), javaFormatter);
	                    generatedJavaFiles.add(gjf);
	                }
            	}
	          }
	        }
			
			units = mybatisConfig.getCompilationUnits(dbTables);
			if(units != null && units.size() > 0){
				for (CompilationUnit compilationUnit : units) {
	                gjf = new GeneratedJavaFile(compilationUnit, mainJavaFile.getAbsolutePath(), engine.getCharCode(), javaFormatter);
	                generatedJavaFiles.add(gjf);
	            }
        	}
			
			units = app.getCompilationUnits();
			if(units != null && units.size() > 0){
				for (CompilationUnit compilationUnit : units) {
					if(compilationUnit.getType().getShortName().equals("AppTest")) {
						gjf = new GeneratedJavaFile(compilationUnit, testJavaFile.getAbsolutePath(), engine.getCharCode(), javaFormatter);
					}else {
						gjf = new GeneratedJavaFile(compilationUnit, mainJavaFile.getAbsolutePath(), engine.getCharCode(), javaFormatter);
					}
	                generatedJavaFiles.add(gjf);
	            }
        	}
        	

			generatedJavaFiles.stream().forEach(curGjf->{
				projects.add(curGjf.getTargetProject());

	            File targetFile;
	            String source;
	            try {
	                File directory = getDirectory(curGjf.getTargetProject(), curGjf.getTargetPackage());
	                targetFile = new File(directory, curGjf.getFileName());
	                if (targetFile.exists()) {
	                    if (isOverwriteEnabled()) {
	                        source = curGjf.getFormattedContent();
	                    } else {
	                        source = curGjf.getFormattedContent();
	                        targetFile = getUniqueFileName(directory, curGjf.getFileName());
	                    }
	                } else {
	                    source = curGjf.getFormattedContent();
	                }

	                writeFile(targetFile, source, curGjf.getFileEncoding());
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public File getDirectory(String targetProject, String targetPackage) throws Exception{

        File project = new File(targetProject);
        if(project.exists()){
        	project.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
            	throw new Exception();
            }
        }
        return directory;
    }
	
	public void generatePom(File project) {
		MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
			Model model = reader.read(new FileReader(ResourceUtils.getFile("classpath:pom-template.xml")));
			
			model.setArtifactId(engine.getArtifactId());
			model.setGroupId(engine.getGroupId());
			model.setVersion(engine.getVersion());
			model.setName(engine.getProjectName());
			
			MavenXpp3Writer out = new MavenXpp3Writer();
			out.write(new FileOutputStream(new File(project, "pom.xml")), model);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
	}

    public boolean isOverwriteEnabled() {
        return true;
    }
    
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    private File getUniqueFileName(File directory, String fileName) throws Exception {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new Exception();
        }

        return answer;
    }
    
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

	public Investigator getInvestigator() {
		return investigator;
	}

	public ICommentGenerator getICommentGenerator() {
		return iCommentGenerator;
	}
}
