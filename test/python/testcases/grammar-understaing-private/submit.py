import os
import zipfile

def zip_txt_files(output_filename="txt_files.zip"):
    # 获取当前目录下的所有文件
    current_dir = os.getcwd()
    txt_files = [f for f in os.listdir(current_dir) if f.endswith(".txt")]

    # 创建压缩文件
    with zipfile.ZipFile(output_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for txt_file in txt_files:
            zipf.write(txt_file)

    print(f"Successfully created {output_filename} containing {len(txt_files)} .txt files.")

if __name__ == "__main__":
    zip_txt_files()
