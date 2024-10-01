import csv

csv_filename = 'out/chi_squared.csv'
data = []

def write(filename: str):
    with open(filename, mode='r', newline='') as file:
        reader = csv.reader(file)
        header = next(reader)
        for row in reader:
            data.append([int(row[0])] + [float(value.replace(',', '')) for value in row[1:]])

    latex_table = "\\centering\n\\begin{tabular}{|c|c|c|c|c|c|}\n"
    latex_table += "\\hline\n"
    latex_table += "k & Observed $P_k$ & Expected $P_k$ & $\\frac{(O-E)^2}{E}$ \\\\\n"
    latex_table += "\\hline\n"

    for row in data:
        latex_table += f"{row[0]} & {row[1]:.10f} & {row[2]:.10f} & {row[3]:.10f}\\\\\n"
        latex_table += "\\hline\n"

    latex_table += "\\end{tabular}\n\\end{table}"

    with open('out/chi_squared_table.tex', 'w') as tex_file:
        tex_file.write(latex_table)

if __name__ == '__main__':
    write(csv_filename)