import os
import zipfile

def zip_src_folder(src_dir, submit_dir, zip_filename):
    # 确保提交目录存在
    os.makedirs(submit_dir, exist_ok=True)

    # 创建 zip 文件的完整路径
    zip_filepath = os.path.join(submit_dir, zip_filename)

    # 创建 zip 文件并添加 src 目录下的所有文件
    with zipfile.ZipFile(zip_filepath, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(src_dir):
            for file in files:
                # 获取文件的完整路径
                file_path = os.path.join(root, file)
                # 将文件加入 zip 文件，并保持相对路径
                zipf.write(file_path, os.path.relpath(file_path, src_dir))

    print(f"压缩完成，压缩包保存到: {zip_filepath}")

# 示例使用
src_directory = 'src'  # 源文件夹路径
submit_directory = 'submit'  # 提交文件夹路径
zip_file_name = 'src_files.zip'  # 压缩包名称

zip_src_folder(src_directory, submit_directory, zip_file_name)
