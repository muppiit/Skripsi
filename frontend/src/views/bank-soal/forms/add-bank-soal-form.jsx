/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Modal,
  Select,
  Table,
  Tabs,
  Row,
  Col,
  Button,
  message,
} from "antd";

import { DeleteOutlined, PlusOutlined } from "@ant-design/icons";
import { reqUserInfo } from "@/api/user";
import { getTahunAjaran } from "@/api/tahun-ajaran";
import { getSemester } from "@/api/semester";
import { getKelas } from "@/api/kelas";
import { getBankSoal } from "@/api/bankSoal";
import { getSoalUjian } from "@/api/soalUjian";
import { getSubjects } from "@/api/subject";
import { getRPSDetail } from "@/api/rpsDetail";
import { getSeason } from "@/api/season";

const { TextArea } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

const AddBankSoalForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [bankSoal, setBankSoal] = useState([]);
  const [form] = Form.useForm();

  const [userSchoolId, setUserSchoolId] = useState([]); // State untuk menyimpan ID sekolah user
  const [subjectList, setSubjectList] = useState([]);
  const [rpsDetailList, setRpsDetailList] = useState([]);
  const [tahunAjaranList, setTahunAjaranList] = useState([]);
  const [semesterList, setSemesterList] = useState([]);
  const [kelasList, setKelasList] = useState([]);
  const [seasonList, setSeasonList] = useState([]);
  const [filteredSeasonList, setFilteredSeasonList] = useState([]);
  const [soalUjianList, setSoalUjianList] = useState([]);
  const [selectedSoalData, setSelectedSoalData] = useState(null); // State untuk menyimpan data soal yang dipilih
  const [selectedNamaUjian, setSelectedNamaUjian] = useState(null); // State untuk nama ujian yang dipilih
  const [filteredSoalList, setFilteredSoalList] = useState([]); // State untuk daftar soal yang difilter berdasarkan nama ujian
  const [tableLoading, setTableLoading] = useState(false);

  const fetchUserInfo = async () => {
    try {
      const response = await reqUserInfo(); // Ambil data user dari API
      setUserSchoolId(response.data.school_id); // Simpan ID sekolah user ke state
      console.log("User School ID: ", response.data.school_id);
    } catch (error) {
      message.error("Gagal mengambil informasi pengguna");
    }
  };

  const fetchSubjectList = async () => {
    try {
      const result = await getSubjects();
      if (result.data.statusCode === 200) {
        setSubjectList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchRpsDetailList = async () => {
    try {
      const result = await getRPSDetail();
      if (result.data.statusCode === 200) {
        setRpsDetailList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchBankSoal = async () => {
    setTableLoading(true);
    try {
      const result = await getBankSoal();
      if (result.data.statusCode === 200) {
        setBankSoal(result.data.content);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setTableLoading(false);
    }
  };

  const fetchKelasList = async () => {
    try {
      const result = await getKelas();
      if (result.data.statusCode === 200) {
        setKelasList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const fetchSeasonList = async () => {
    try {
      const result = await getSeason();
      if (result.data.statusCode === 200) {
        setSeasonList(result.data.content || []);
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  const filterSeasonOptions = (semesterId, kelasId) => {
    const filtered = (seasonList || []).filter((item) => {
      const matchesSemester = !semesterId || item?.semester?.idSemester === semesterId;
      const matchesKelas = !kelasId || item?.kelas?.idKelas === kelasId;
      return matchesSemester && matchesKelas;
    });

    setFilteredSeasonList(filtered);
  };

  const fetchSoalUjian = async () => {
    try {
      const result = await getSoalUjian();
      if (result.data.statusCode === 200) {
        setSoalUjianList(result.data.content);
      } else {
        message.error("Gagal mengambil data");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    }
  };

  // Handler untuk ketika user memilih nama ujian
  const handleNamaUjianChange = (value) => {
    setSelectedNamaUjian(value);

    // Filter soal berdasarkan nama ujian yang dipilih
    const filtered = soalUjianList.filter((soal) => soal.namaUjian === value);
    setFilteredSoalList(filtered);

    // Reset pertanyaan yang dipilih dan data soal
    setSelectedSoalData(null);
    form.setFieldsValue({
      idSoalUjian: undefined,
      pertanyaan: undefined,
      bobot: undefined,
      jenisSoal: undefined,
    });
  };

  // Handler untuk ketika user memilih pertanyaan
  const handlePertanyaanChange = (value) => {
    const selectedSoal = filteredSoalList.find(
      (soal) => soal.idSoalUjian === value
    );
    if (selectedSoal) {
      setSelectedSoalData(selectedSoal);

      // Set form fields dengan data yang dipilih
      form.setFieldsValue({
        idSoalUjian: selectedSoal.idSoalUjian,
        pertanyaan: selectedSoal.pertanyaan,
        bobot: selectedSoal.bobot,
        jenisSoal: selectedSoal.jenisSoal,
        opsi: selectedSoal.opsi,
        pasangan: selectedSoal.pasangan,
        toleransiTypo: selectedSoal.toleransiTypo,
        jawabanBenar: selectedSoal.jawabanBenar,
        idTaksonomi: selectedSoal.taksonomi?.idTaksonomi,
        idStudyProgram: selectedSoal.school?.idSchool || userSchoolId,
      });
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setTableLoading(true);
        const [tahunAjaranRes, semesterRes] = await Promise.all([
          getTahunAjaran(),
          getSemester(),
        ]);

        setTahunAjaranList(tahunAjaranRes.data.content || []);
        setSemesterList(semesterRes.data.content || []);
      } catch (error) {
        message.error("Gagal memuat data");
      } finally {
        setTableLoading(false);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    fetchUserInfo();
    fetchSubjectList();
    fetchRpsDetailList();
    fetchKelasList();
    fetchSeasonList();
    fetchSoalUjian();
    fetchBankSoal();
  }, []);

  useEffect(() => {
    const semesterId = form.getFieldValue("idSemester");
    const kelasId = form.getFieldValue("idKelas");
    filterSeasonOptions(semesterId, kelasId);
  }, [seasonList]);

  useEffect(() => {
    if (userSchoolId) {
      form.setFieldsValue({ idStudyProgram: userSchoolId });
    }
  }, [userSchoolId, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      // Function to properly format jawabanBenar
      const formatJawabanBenar = (jawabanBenar) => {
        if (!jawabanBenar) return [];

        // If it's already an array, return as is
        if (Array.isArray(jawabanBenar)) {
          return jawabanBenar;
        }

        // If it's a string, convert to array
        if (typeof jawabanBenar === "string") {
          return [jawabanBenar];
        }

        // Default to empty array
        return [];
      };

      // Function to properly format opsi
      const formatOpsi = (opsi) => {
        if (!opsi) return {};

        // If it's already an object, return as is
        if (typeof opsi === "object" && !Array.isArray(opsi)) {
          return opsi;
        }

        // Default to empty object
        return {};
      };

      // Function to properly format pasangan
      const formatPasangan = (pasangan) => {
        if (!pasangan) return {};

        // If it's already an object, return as is
        if (typeof pasangan === "object" && !Array.isArray(pasangan)) {
          return pasangan;
        }

        // Default to empty object
        return {};
      };

      const payload = {
        idBankSoal: null,
        idSoalUjian: values.idSoalUjian,
        namaUjian: values.namaUjian,
        pertanyaan: values.pertanyaan,
        bobot: values.bobot?.toString(),
        jenisSoal: values.jenisSoal,
        opsi: formatOpsi(values.opsi),
        pasangan: formatPasangan(values.pasangan),
        toleransiTypo: values.toleransiTypo,
        jawabanBenar: formatJawabanBenar(values.jawabanBenar),
        idStudyProgram: values.idStudyProgram,
        idTaksonomi: values.idTaksonomi,
        idSubject: values.idSubject,
        idRpsDetail: values.idRpsDetail,
        idTahun: values.idTahun,
        idSemester: values.idSemester,
        idKelas: values.idKelas,
        idSeason: values.idSeason,
        idSchool: values.idStudyProgram,
      };

      console.log("Formatted payload:", payload); // For debugging
      onOk(payload);
    } catch (error) {
      console.error("Validation failed:", error);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    setSelectedSoalData(null);
    setSelectedNamaUjian(null);
    setFilteredSoalList([]);
    setFilteredSeasonList([]);
    onCancel();
  };

  return (
    <Modal
      title="Kaitkan Soal dengan ATP"
      open={visible}
      onCancel={handleCancel}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      width={1000} // Mengatur lebar modal agar lebih luas
    >
      <Form form={form} layout="vertical">
        <Tabs defaultActiveKey="1">
          <TabPane tab="Informasi Relasi" key="1">
            <Row gutter={16}>
              <Col xs={24} sm={24} md={24}>
                <Form.Item
                  label="Study Program:"
                  name="idStudyProgram"
                  style={{ display: "none" }}
                  rules={[
                    { required: true, message: "Silahkan pilih study program" },
                  ]}
                >
                  <Select defaultValue={userSchoolId} disabled>
                    {userSchoolId && (
                      <Option key={userSchoolId} value={userSchoolId}>
                        {userSchoolId}
                      </Option>
                    )}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item
                  label="Subject:"
                  name="idSubject"
                  rules={[{ required: true, message: "Silahkan pilih Subject" }]}
                >
                  <Select placeholder="Pilih Subject" showSearch optionFilterProp="children">
                    {subjectList.map((item) => (
                      <Option key={item.id} value={item.id}>
                        {item.name}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item
                  label="RPS Detail:"
                  name="idRpsDetail"
                  rules={[
                    { required: true, message: "Silahkan pilih RPS Detail" },
                  ]}
                >
                  <Select placeholder="Pilih RPS Detail" showSearch optionFilterProp="children">
                    {rpsDetailList.map((item) => (
                      <Option key={item.id} value={item.id}>
                        {item.weekLabel ? `${item.weekLabel} - ` : ""}
                        {item.sub_cp_mk || item.id}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item
                  label="Tahun Ajaran:"
                  name="idTahun"
                  rules={[{ required: true, message: "Silahkan pilih Tahun Ajaran" }]}
                >
                  <Select placeholder="Pilih Tahun Ajaran" showSearch optionFilterProp="children">
                    {tahunAjaranList.map((item) => (
                      <Option key={item.idTahun} value={item.idTahun}>
                        {item.tahunAjaran}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item
                  label="Semester:"
                  name="idSemester"
                  rules={[{ required: true, message: "Silahkan pilih Semester" }]}
                >
                  <Select
                    placeholder="Pilih Semester"
                    showSearch
                    optionFilterProp="children"
                    onChange={(value) => {
                      const kelasId = form.getFieldValue("idKelas");
                      form.setFieldsValue({ idSeason: undefined });
                      filterSeasonOptions(value, kelasId);
                    }}
                  >
                    {semesterList.map((item) => (
                      <Option key={item.idSemester} value={item.idSemester}>
                        {item.namaSemester}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item
                  label="Kelas:"
                  name="idKelas"
                  rules={[{ required: true, message: "Silahkan pilih Kelas" }]}
                >
                  <Select
                    placeholder="Pilih Kelas"
                    showSearch
                    optionFilterProp="children"
                    onChange={(value) => {
                      const semesterId = form.getFieldValue("idSemester");
                      form.setFieldsValue({ idSeason: undefined });
                      filterSeasonOptions(semesterId, value);
                    }}
                  >
                    {kelasList.map((item) => (
                      <Option key={item.idKelas} value={item.idKelas}>
                        {item.namaKelas}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={24} md={12}>
                <Form.Item label="Season (Opsional):" name="idSeason">
                  <Select
                    placeholder="Pilih Season"
                    showSearch
                    optionFilterProp="children"
                    allowClear
                  >
                    {(filteredSeasonList.length > 0 ? filteredSeasonList : seasonList).map((item) => (
                      <Option key={item.idSeason} value={item.idSeason}>
                        {(item.kelas?.namaKelas || "-") + " | " + (item.semester?.namaSemester || "-")}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          </TabPane>
          <TabPane tab="Informasi Soal" key="2">
            <Row gutter={16}>
              <Col xs={24} sm={24} md={24}>
                <Form.Item
                  label="Nama Ujian:"
                  name="namaUjian"
                  rules={[
                    {
                      required: true,
                      message: "Silahkan pilih Nama Ujian",
                    },
                  ]}
                >
                  <Select
                    placeholder="Pilih Nama Ujian"
                    showSearch
                    optionFilterProp="children"
                    onChange={handleNamaUjianChange}
                    filterOption={(input, option) =>
                      option.children
                        .toLowerCase()
                        .includes(input.toLowerCase())
                    }
                  >
                    {/* Menampilkan nama ujian unik saja */}
                    {[
                      ...new Set(soalUjianList.map((soal) => soal.namaUjian)),
                    ].map((namaUjian) => (
                      <Option key={namaUjian} value={namaUjian}>
                        {namaUjian}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>

              {/* Form Pertanyaan - muncul setelah nama ujian dipilih */}
              {selectedNamaUjian && (
                <Col xs={24} sm={24} md={24}>
                  <Form.Item
                    label="Pertanyaan:"
                    name="idSoalUjian"
                    rules={[
                      {
                        required: true,
                        message: "Silahkan pilih Pertanyaan",
                      },
                    ]}
                  >
                    <Select
                      placeholder="Pilih Pertanyaan"
                      showSearch
                      optionFilterProp="children"
                      onChange={handlePertanyaanChange}
                      filterOption={(input, option) =>
                        option.children
                          .toLowerCase()
                          .includes(input.toLowerCase())
                      }
                    >
                      {filteredSoalList.map(({ idSoalUjian, pertanyaan }) => (
                        <Option key={idSoalUjian} value={idSoalUjian}>
                          {pertanyaan}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              )}
              {/* Informasi detail soal - muncul setelah pertanyaan dipilih */}
              {selectedSoalData && (
                <>
                  <Col xs={24} sm={24} md={24}>
                    <Form.Item label="Detail Pertanyaan:" name="pertanyaan">
                      <TextArea
                        rows={4}
                        disabled
                        style={{ backgroundColor: "#f5f5f5" }}
                      />
                    </Form.Item>
                  </Col>

                  <Col xs={24} sm={12} md={12}>
                    <Form.Item label="Bobot:" name="bobot">
                      <Input disabled style={{ backgroundColor: "#f5f5f5" }} />
                    </Form.Item>
                  </Col>

                  <Col xs={24} sm={12} md={12}>
                    <Form.Item label="Jenis Soal:" name="jenisSoal">
                      <Input disabled style={{ backgroundColor: "#f5f5f5" }} />
                    </Form.Item>
                  </Col>

                  {/* Tampilkan opsi jika ada */}
                  {selectedSoalData.opsi &&
                    Object.keys(selectedSoalData.opsi).length > 0 && (
                      <Col xs={24} sm={24} md={24}>
                        <Form.Item label="Opsi Jawaban:">
                          <div
                            style={{
                              padding: "12px",
                              border: "1px solid #d9d9d9",
                              borderRadius: "6px",
                              backgroundColor: "#f9f9f9",
                            }}
                          >
                            {Object.entries(selectedSoalData.opsi).map(
                              ([key, value]) => (
                                <div
                                  key={key}
                                  style={{
                                    marginBottom: "8px",
                                    padding: "4px 8px",
                                    backgroundColor: "#fff",
                                    borderRadius: "4px",
                                  }}
                                >
                                  <strong style={{ color: "#1890ff" }}>
                                    {key}:
                                  </strong>{" "}
                                  {value}
                                </div>
                              )
                            )}
                          </div>
                        </Form.Item>
                      </Col>
                    )}

                  {/* Tampilkan jawaban benar */}
                  {selectedSoalData.jawabanBenar && (
                    <Col xs={24} sm={24} md={24}>
                      <Form.Item label="Jawaban Benar:">
                        <div
                          style={{
                            padding: "12px",
                            border: "1px solid #52c41a",
                            borderRadius: "6px",
                            backgroundColor: "#f6ffed",
                          }}
                        >
                          <strong style={{ color: "#52c41a" }}>
                            {Array.isArray(selectedSoalData.jawabanBenar)
                              ? selectedSoalData.jawabanBenar.join(", ")
                              : selectedSoalData.jawabanBenar}
                          </strong>
                        </div>
                      </Form.Item>
                    </Col>
                  )}
                </>
              )}
            </Row>
          </TabPane>
        </Tabs>

        {/* Hidden fields untuk mengirim data lengkap */}
        <Form.Item name="opsi" style={{ display: "none" }}>
          <Input />
        </Form.Item>
        <Form.Item name="pasangan" style={{ display: "none" }}>
          <Input />
        </Form.Item>
        <Form.Item name="toleransiTypo" style={{ display: "none" }}>
          <Input />
        </Form.Item>
        <Form.Item name="jawabanBenar" style={{ display: "none" }}>
          <Input />
        </Form.Item>
        <Form.Item name="idTaksonomi" style={{ display: "none" }}>
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddBankSoalForm;
