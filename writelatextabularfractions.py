import csv

csv_filename = 'out/fractions.csv'
data = []

def write(filename: str):
    with open(filename, mode='r', newline='') as file:
        reader = csv.reader(file)
        header = next(reader)
        data = [row for row in reader]

    latex_table = "\\centering\n\\begin{tabular}{|c|c|c|c|c|c|}\n"
    latex_table += "\\hline\n"
    latex_table += "p & n & onediv & twodiv & trials \\\\\n"
    latex_table += "\\hline\n"

    for row in data:
        latex_table += f"{row[0]} & {row[1]} & {row[2]} & {row[3]} & {row[4]}\\\\\n"
        latex_table += "\\hline\n"

    latex_table += "\\end{tabular}\n\\end{table}"

    with open('out/table.tex', 'w') as tex_file:
        tex_file.write(latex_table)

if __name__ == '__main__':
    write(csv_filename)